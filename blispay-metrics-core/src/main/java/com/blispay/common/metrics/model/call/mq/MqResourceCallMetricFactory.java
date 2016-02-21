package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.InfraMetricName;
import com.blispay.common.metrics.model.MetricGroup;

public class MqResourceCallMetricFactory extends BaseMetricFactory<MqResourceCallMetric, MqResourceCallEventData> {

    public MqResourceCallMetricFactory(final MetricGroup group, final String name) {
        super(group, name);
    }

    public MqResourceCallMetric newMetric(final MqResourceCallEventData eventData) {
        return new MqResourceCallMetric(timestamp(), getGroup(), getName(), eventData);
    }

}
