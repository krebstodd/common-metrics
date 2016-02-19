package com.blispay.common.metrics.event;

public interface EventFilter {

    Boolean acceptsEvent(MetricEvent event);

}