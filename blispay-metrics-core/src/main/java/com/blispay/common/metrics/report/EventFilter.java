package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.MetricEvent;

public interface EventFilter {

    Boolean acceptsEvent(MetricEvent event);

}
