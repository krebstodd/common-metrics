package com.blispay.common.metrics.data;

import com.blispay.common.metrics.model.BaseMetricModel;

public interface MetricSerializer {

    public String serialize(BaseMetricModel metric);

}
