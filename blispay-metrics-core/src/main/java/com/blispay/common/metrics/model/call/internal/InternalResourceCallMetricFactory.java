package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.InfraMetricName;
import com.blispay.common.metrics.model.MetricGroup;

public class InternalResourceCallMetricFactory extends BaseMetricFactory<InternalResourceCallMetric, InternalResourceCallEventData> {

    public InternalResourceCallMetricFactory(final MetricGroup group, final String name) {
        super(group, name);
    }

    public InternalResourceCallMetric newMetric(final InternalResourceCallEventData eventData) {
        return new InternalResourceCallMetric(timestamp(), getGroup(), getName(), eventData);
    }

}
