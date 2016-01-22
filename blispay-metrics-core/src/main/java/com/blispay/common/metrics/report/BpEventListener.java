package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.MetricEvent;

import java.util.Collection;

public interface BpEventListener {

    void acceptEvent(MetricEvent event);

    Collection<EventFilter> getFilters();

}
