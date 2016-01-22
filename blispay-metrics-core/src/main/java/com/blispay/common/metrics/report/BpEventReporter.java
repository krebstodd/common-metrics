package com.blispay.common.metrics.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class BpEventReporter implements BpEventListener {

    private final Set<EventFilter> filters = new HashSet<>();

    public void addFilter(final EventFilter filter) {
        this.filters.add(filter);
    }

    @Override
    public Collection<EventFilter> getFilters() {
        return this.filters;
    }

}
