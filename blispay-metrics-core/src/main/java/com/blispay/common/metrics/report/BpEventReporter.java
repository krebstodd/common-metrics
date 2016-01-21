package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class BpEventReporter {

    private final Set<EventFilter> filters = new HashSet<>();

    public abstract void reportEvent(final RecordableEvent event);

    public void addFilter(final EventFilter filter) {
        this.filters.add(filter);
    }

    public Set<EventFilter> getFilters() {
        return this.filters;
    }

}
