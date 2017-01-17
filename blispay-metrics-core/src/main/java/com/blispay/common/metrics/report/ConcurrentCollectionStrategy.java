package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Class ConcurrentCollectionStrategy.
 */
public class ConcurrentCollectionStrategy implements SnapshotCollectionStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentCollectionStrategy.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private final ExecutorService service;
    private final Long timeout;
    private final TimeUnit timeoutUnit;

    /**
     * Constructs ConcurrentCollectionStrategy.
     *
     * @param numThreads numThreads.
     */
    public ConcurrentCollectionStrategy(final Integer numThreads) {
        this(numThreads, DEFAULT_TIMEOUT);
    }

    /**
     * Multi threaded collection strategy.
     *
     * @param numThreads Fixed thread pool size.
     * @param timeout Max timeout for collection.
     */
    public ConcurrentCollectionStrategy(final Integer numThreads, final Duration timeout) {

        this.service = multiThreadedExecutor(numThreads);
        this.timeout = timeout.toMillis();
        this.timeoutUnit = TimeUnit.MILLISECONDS;
    }

    @Override
    public Set<EventModel> performCollection(final Collection<SnapshotProvider> snapshotProviders) {

        try {

            // Put the snapshots providers into an ordered list and then create a list of callable wrappers around
            // the provider that can be invoked by the executor service.
            final List<SnapshotProvider> orderedProviders = new ArrayList<>(snapshotProviders);
            final List<CallableSnapshotWrapper> callableSnapshotWrappers = orderedProviders.stream()
                    .map(CallableSnapshotWrapper::new)
                    .collect(Collectors.toList());

            // Invoke all provider callable wrappers. The returned list of futures is in the same sequential order as
            // produced by the iterator for the callable list enabling us to match providers with their corresponding future
            // for logging purposes.
            final List<Future<EventModel>> futures = service.invokeAll(callableSnapshotWrappers, timeout, timeoutUnit);

            // Loop through the futures and get the results of the provider. The ExecutorService#invokeAll method
            // will block the current thread until either all callables resolve or the timeout is reached.
            // For any callables that result in an exception -OR- are not completed by the timeout, the Future#get function
            // will throw a corresponding exception. The getResultIfAvailable method will return the resulting model if
            // it is available and will catch/log any exceptions with the provider id/desc should the result not be available.
            final Set<EventModel> results = new HashSet<>();
            for (int i = 0; i < snapshotProviders.size(); i++) {
                final SnapshotProvider correspondingProvider = orderedProviders.get(i);
                final Optional<EventModel> model = getResultIfAvailable(correspondingProvider.id(), correspondingProvider.description(), futures.get(i));
                model.ifPresent(results::add);
            }

            return results;

        // The current thread was interrupted while blocking for the results of the ExecutorService#invokeAll call.
        // the invokeAll method blocks the current thread until either all callables complete or the timeout
        // is reached. This exception will only be thrown if someone else explicitly interrupts the current thread while it's
        // waiting for invoke all to complete. 
        } catch (InterruptedException e) {
            LOG.error("Caught interrupted exception attempting to perform snapshot collection.", e);
            return new HashSet<>();
        }

    }

    private Optional<EventModel> getResultIfAvailable(final String providerId,
                                                      final String providerDescription,
                                                      final Future<EventModel> future) {

        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            LOG.error("Caught exception attempting to get the results of snapshot provider with id [{}] and description [{}].",
                    providerId, providerDescription, e);
        }

        if (!future.isDone() && !future.isCancelled()) {
            future.cancel(Boolean.TRUE);
        }

        return Optional.empty();
    }

    private static ExecutorService multiThreadedExecutor(final Integer numThreads) {
        return Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public Duration getTimeout() {
        return Duration.ofMillis(this.timeout);
    }

    /**
     * Wraps a snapshot provider and implements the Callable interface so that the provider can be submitted to
     * an executor service and invoked in a background thread.
     */
    private static final class CallableSnapshotWrapper implements Callable<EventModel> {

        private final SnapshotProvider provider;

        private CallableSnapshotWrapper(final SnapshotProvider provider) {
            this.provider = provider;
        }

        @Override
        public EventModel call() {
            return provider.snapshot();
        }

    }


}
