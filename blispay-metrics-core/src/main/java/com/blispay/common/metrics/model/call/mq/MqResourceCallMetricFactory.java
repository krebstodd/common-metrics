package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.MetricGroup;

public class MqResourceCallMetricFactory extends BaseMetricFactory<MqResourceCallMetric, MqResourceCallEventData> {

    public MqResourceCallMetricFactory(final String application, final MetricGroup group, final String name) {
        super(application, group, name);
    }

    public MqResourceCallMetric newMetric(final MqResourceCallEventData eventData) {
        return new MqResourceCallMetric(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }

}
