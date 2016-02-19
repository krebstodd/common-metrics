package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventFilter;
import com.blispay.common.metrics.event.EventListener;
import com.blispay.common.metrics.event.MetricEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class TestMetricEventListener implements EventListener {

    private final Set<EventFilter> filters = new HashSet<>();
    private final LinkedList<MetricEvent> events = new LinkedList<>();

    @Override
    public void acceptEvent(final MetricEvent event) {
        events.add(event);
    }

    @Override
    public Collection<EventFilter> getFilters() {
        return filters;
    }

    public void addFilter(final EventFilter filter) {
        this.filters.add(filter);
    }

    public LinkedList<MetricEvent> history() {
        return events;
    }

}