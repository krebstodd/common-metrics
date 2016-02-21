package com.blispay.common.metrics.model.utilization;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.InfraMetricName;
import com.blispay.common.metrics.model.MetricGroup;

public class ResourceUtilizationMetricFactory extends BaseMetricFactory<ResourceUtilizationMetric, ResourceUtilizationData> {

    public ResourceUtilizationMetricFactory(final MetricGroup group, final String name) {
        super(group, name);
    }

    public ResourceUtilizationMetric newMetric(final ResourceUtilizationData eventData) {
        return new ResourceUtilizationMetric(timestamp(), getGroup(), getName(), eventData);
    }
}
