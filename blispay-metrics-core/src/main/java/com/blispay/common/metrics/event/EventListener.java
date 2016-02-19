package com.blispay.common.metrics.event;

import java.util.Collection;

public interface EventListener {

    Collection<EventFilter> getFilters();

    void acceptEvent(final MetricEvent event);

}
