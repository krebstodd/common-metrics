package com.blispay.common.metrics.event;

public interface EventEmitter {

    void emit(final MetricEvent event);

}
