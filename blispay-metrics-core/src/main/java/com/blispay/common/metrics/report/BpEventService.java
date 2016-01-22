package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.MetricEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class BpEventService {

    private final Set<BpEventListener> eventListeners = new HashSet<>();

    /**
     * Accept a new event.
     * @param event The new event.
     */
    public void acceptEvent(final MetricEvent event) {
        eventListeners.stream()
                .filter(reporter -> passesFilters(reporter, event))
                .forEach(reporter -> reporter.acceptEvent(event));
    }

    public void addEventListener(final BpEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(final BpEventListener listener) {
        eventListeners.remove(listener);
    }

    private static Boolean passesFilters(final BpEventListener reporter, final MetricEvent event) {
        return reporter.getFilters()
                .stream()
                .allMatch(filter -> filter.acceptsEvent(event));
    }

}
