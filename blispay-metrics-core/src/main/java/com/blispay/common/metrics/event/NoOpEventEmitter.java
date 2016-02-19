package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;

public class NoOpEventEmitter implements EventEmitter {

    @Override
    public void emit(final BaseMetricModel event) {

    }

}
