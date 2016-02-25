package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;

public class MetricRepository {

    private final EventEmitter eventEmitter;
    private Boolean eventsEnabled = Boolean.TRUE;

    public MetricRepository(final EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    protected void save(final BaseMetricModel event) {
        if (eventsEnabled) {
            this.eventEmitter.emit(event);
        }
    }

    public void disableEvents() {
        this.eventsEnabled = Boolean.FALSE;
    }

    public void enableEvents() {
        this.eventsEnabled = Boolean.TRUE;
    }

}
