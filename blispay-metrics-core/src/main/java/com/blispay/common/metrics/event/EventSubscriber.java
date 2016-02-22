package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;

import java.util.Collection;
import java.util.LinkedList;

public interface EventSubscriber {

    default Collection<EventFilter> getFilters() {
        return new LinkedList<>();
    }

    void acceptEvent(final BaseMetricModel event);

}
