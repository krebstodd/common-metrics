package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
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

            return service.invokeAll(snapshotProviders, timeout, timeoutUnit).stream().map(this::getOrTimeout).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());

        } catch (InterruptedException e) {

            LOG.error("Caught interrupted exception attempting to perform snapshot collection.", e);
            throw new IllegalStateException(e);

        }

    }

    private Optional<EventModel> getOrTimeout(final Future<EventModel> future) {

        try {

            return Optional.ofNullable(future.get());

        } catch (InterruptedException e) {
            LOG.error("Caught interrupted exception waiting for snapshot event result.", e);
        } catch (ExecutionException e) {
            LOG.error("Caught execution exception attempting to collect snapshot event.", e.getCause());
        } catch (CancellationException e) {
            LOG.error("Caught cancellation exception due to one or more snapshot provider timeouts.", e);
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

}
