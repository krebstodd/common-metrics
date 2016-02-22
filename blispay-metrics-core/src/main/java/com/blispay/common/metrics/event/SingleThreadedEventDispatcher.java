package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class SingleThreadedEventDispatcher extends EventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(SingleThreadedEventDispatcher.class);

    final Set<EventSubscriber> eventListeners;

    public SingleThreadedEventDispatcher() {
        this.eventListeners = new HashSet<>();
    }

    @Override
    public void dispatch(final BaseMetricModel evt) {

        if (!isRunning()) {
            throw new IllegalStateException("Dispatcher not yet started");
        }

        eventListeners.stream()
                .filter(reporter -> passesFilters(reporter, evt))
                .forEach(reporter -> {

                    // TODO - Collect failed dispatches and retry w/ backoff, eventually just logging them as error and moving on.
                    try {
                        reporter.acceptEvent(evt);
                    } catch (Exception ex) {
                        LOG.error("Received error attempting to accept event [{}]", evt.getName().toString());
                    }

                });
    }

    @Override
    public EventEmitter newEventEmitter() {
        return this::dispatch;
    }

    @Override
    public void subscribe(final EventSubscriber listener) {
        eventListeners.add(listener);
    }

    private static Boolean passesFilters(final EventSubscriber reporter, final BaseMetricModel event) {
        return reporter.getFilters()
                .stream()
                .allMatch(filter -> safeFilter(filter, event));
    }

    private static Boolean safeFilter(final EventFilter filter, final BaseMetricModel model) {
        try {

            return filter.acceptsEvent(model);

        } catch (Exception ex) {
            LOG.error("Caught exception determining whether filter can accept event occurring on [{}]", model.getTimestamp(), ex);
            return Boolean.FALSE;
        }
    }

    @Override
    public void stop(final Runnable runnable) {

        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            // TODO - Flush the collections of failed events awaiting retry
            LOG.info("Dispatcher stopped.");

            runnable.run();

        } else {
            LOG.warn("Dispatcher is already stopped");
            runnable.run();
        }

    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            LOG.info("Dispatcher started.");

        } else {
            LOG.warn("Dispatcher is already running.");
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            LOG.info("Dispatcher stopped.");

            // TODO - Flush the collections of failed events awaiting retry

        } else {
            LOG.warn("Dispatcher is already stopped.");
        }
    }

}
