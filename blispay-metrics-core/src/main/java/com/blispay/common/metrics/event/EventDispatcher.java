package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class EventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);

    final Set<EventSubscriber> eventListeners;

    public EventDispatcher() {
        this.eventListeners = new HashSet<>();
    }

    // TODO - If we ever have listeners that do longer running things than simple SLF4J logging, we'll need to put a thread pool in here.
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

    public EventEmitter newEventEmitter() {
        return this::dispatch;
    }

    public void addListener(final EventSubscriber listener) {
        eventListeners.add(listener);
    }

    private static Boolean passesFilters(final EventSubscriber reporter, final BaseMetricModel event) {
        return reporter.getFilters()
                .stream()
                .allMatch(filter -> filter.acceptsEvent(event));
    }

}
