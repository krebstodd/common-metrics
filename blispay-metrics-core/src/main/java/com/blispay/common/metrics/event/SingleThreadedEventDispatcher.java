package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.util.NotYetStartedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Note - This is essentially a toy dispatcher that really should only ever be used when all subscribers do something REALLY
 *        simple (logging). If any subscribers execute long running or error prone processes (http), using the events they
 *        receive, a multi threaded dispatcher should be implemented w/ thread pooling, retries, persistence for failed events, etc.
 */
public class SingleThreadedEventDispatcher extends EventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(SingleThreadedEventDispatcher.class);

    private final Set<EventSubscriber> eventListeners;

    public SingleThreadedEventDispatcher() {
        this.eventListeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void dispatch(final EventModel evt) {
        LOG.debug("Dispatching new new event name=[{}]", evt.getHeader().getName());
        if (!isRunningAtomic().get()) {
            throw new NotYetStartedException("Dispatcher not yet started");
        }

        eventListeners.stream()
                .filter(reporter -> passesFilters(reporter, evt))
                .forEach(reporter -> acceptEvent(reporter, evt));
    }

    @Override
    public EventEmitter newEventEmitter() {
        return this::dispatch;
    }

    @Override
    public void subscribe(final EventSubscriber listener) {
        eventListeners.add(listener);
    }

    @Override
    public void unSubscribe(final EventSubscriber listener) {
        eventListeners.remove(listener);
    }


    private void acceptEvent(final EventSubscriber subscriber, final EventModel evt) {
        try {

            subscriber.acceptEvent(evt);

        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {

            LOG.error("Received error attempting to accept event [{}]", evt.getHeader().getName());

        }
        // CHECK_ON: IllegalCatch
    }

    private Boolean passesFilters(final EventSubscriber reporter, final EventModel event) {
        return reporter.getFilters()
                .stream()
                .allMatch(filter -> safeFilter(filter, event));
    }

    private static Boolean safeFilter(final EventFilter filter, final EventModel model) {

        try {

            return filter.acceptsEvent(model);

        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {

            LOG.error("Caught exception determining whether filter can accept event occurring on [{}]", model.getHeader().getTimestamp(), ex);
            return Boolean.FALSE;

        }
        // CHECK_ON: IllegalCatch

    }

    @Override
    public void stop(final Runnable runnable) {

        LOG.info("Stopping event dispatcher...");

        if (isRunningAtomic().compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            // TODO - Flush the collections of failed events awaiting retry
            runnable.run();

        } else {
            LOG.warn("Dispatcher is already stopped");
            runnable.run();
        }

        LOG.info("Dispatcher stopped.");

    }

    @Override
    public void stop() {
        stop(() -> { });
    }

    @Override
    public void start() {

        LOG.info("Starting event dispatcher...");

        if (!isRunningAtomic().compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            LOG.warn("Dispatcher is already running.");
        }

        LOG.info("Dispatcher started.");

    }



}
