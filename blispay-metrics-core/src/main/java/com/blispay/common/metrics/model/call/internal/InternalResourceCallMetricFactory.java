package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.MetricGroup;

public class InternalResourceCallMetricFactory extends BaseMetricFactory<InternalResourceCallMetric, InternalResourceCallEventData> {

    public InternalResourceCallMetricFactory(final String application, final MetricGroup group, final String name) {
        super(application, group, name);
    }

    public InternalResourceCallMetric newMetric(final InternalResourceCallEventData eventData) {
        return new InternalResourceCallMetric(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }

}
