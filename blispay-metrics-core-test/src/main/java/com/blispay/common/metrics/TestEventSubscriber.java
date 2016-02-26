package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventFilter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.model.BaseMetricModel;

import java.util.LinkedList;
import java.util.List;

public class TestEventSubscriber implements EventSubscriber {

    private final List<EventFilter> filters = new LinkedList<>();
    private final LinkedList<BaseMetricModel> events = new LinkedList<>();
    private Boolean exceptionsOn = Boolean.FALSE;

    @Override
    public void acceptEvent(final BaseMetricModel event) {
        if (exceptionsOn) {
            throw new IllegalStateException("Some exception");
        }

        events.add(event);
    }

    @Override
    public List<EventFilter> getFilters() {
        return this.filters;
    }

    public void addFilter(final EventFilter filter) {
        this.filters.add(filter);
    }

    public BaseMetricModel poll() {
        return events.poll();
    }

    public int count() {
        return events.size();
    }

    public List<BaseMetricModel> peek() {
        return this.events;
    }

    public void exceptionsOn(final Boolean throwException) {
        exceptionsOn = throwException;
    }

}