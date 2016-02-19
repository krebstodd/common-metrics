package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;

import java.util.Collection;

public interface EventSubscriber {

    Collection<EventFilter> getFilters();

    void acceptEvent(final BaseMetricModel event);

}
