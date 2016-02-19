package com.blispay.common.metrics.event;

public class NoOpEventEmitter implements EventEmitter {

    @Override
    public void emit(final MetricEvent event) {

    }

}
