package com.blispay.common.metrics.model.utilization;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.MetricGroup;

public class ResourceUtilizationMetricFactory extends BaseMetricFactory<ResourceUtilizationMetric, ResourceUtilizationData> {

    public ResourceUtilizationMetricFactory(final String application, final MetricGroup group, final String name) {
        super(application, group, name);
    }

    public ResourceUtilizationMetric newMetric(final ResourceUtilizationData eventData) {
        return new ResourceUtilizationMetric(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }
}
