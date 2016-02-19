package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;

public class MetricRepository {

    private final EventEmitter eventEmitter;

    public MetricRepository(final EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    protected void save(final BaseMetricModel event) {
        this.eventEmitter.emit(event);
    }

}
