package com.blispay.common.metrics.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class EventDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);

    final Set<EventListener> eventListeners;

    public EventDispatcher() {
        this.eventListeners = new HashSet<>();
    }

    // TODO - If we ever have listeners that do longer running things than simple SLF4J logging, we'll need to put a thread pool in here.
    public void dispatch(final MetricEvent evt) {
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

    public void addListener(final EventListener listener) {
        eventListeners.add(listener);
    }

    private static Boolean passesFilters(final EventListener reporter, final MetricEvent event) {
        return reporter.getFilters()
                .stream()
                .allMatch(filter -> filter.acceptsEvent(event));
    }

}
