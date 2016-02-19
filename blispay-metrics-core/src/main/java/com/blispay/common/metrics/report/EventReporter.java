package com.blispay.common.metrics.report;

import com.blispay.common.metrics.event.EventFilter;
import com.blispay.common.metrics.event.EventSubscriber;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class EventReporter implements EventSubscriber {

    private List<EventFilter> filters = new LinkedList<>();

    public void addFilter(final EventFilter filter) {
        this.filters.add(filter);
    }

    @Override
    public Collection<EventFilter> getFilters() {
        return filters;
    }

}
