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
        eventListeners.stream()
                .filter(reporter -> passesFilters(reporter, evt))
                .forEach(reporter -> {

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
                .allMatch(filter -> filter.acceptsEvent(event));
    }

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public void stop(final Runnable runnable) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public int getPhase() {
        return 0;
    }
}
