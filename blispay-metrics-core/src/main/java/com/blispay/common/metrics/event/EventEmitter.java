package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;

public interface EventEmitter {

    public void emit(final BaseMetricModel event);

}
