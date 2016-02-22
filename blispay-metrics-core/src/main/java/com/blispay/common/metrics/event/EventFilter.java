package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;

public interface EventFilter {

    Boolean acceptsEvent(BaseMetricModel event);

}