package com.blispay.common.metrics.model.counter;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.InfraMetricName;
import com.blispay.common.metrics.model.MetricGroup;

public class ResourceCounterMetricFactory extends BaseMetricFactory<ResourceCounterMetric, ResourceCounterEventData> {

    public ResourceCounterMetricFactory(final MetricGroup group, final String name) {
        super(group, name);
    }

    public ResourceCounterMetric newMetric(final Double count) {
        return newMetric(new ResourceCounterEventData(count));
    }

    public ResourceCounterMetric newMetric(final Long count) {
        return newMetric(new ResourceCounterEventData(Double.valueOf(count)));
    }

    public ResourceCounterMetric newMetric(final Integer count) {
        return newMetric(new ResourceCounterEventData(Double.valueOf(count)));
    }

    public ResourceCounterMetric newMetric(final ResourceCounterEventData count) {
        return new ResourceCounterMetric(timestamp(), getGroup(), getName(), count);
    }
}
