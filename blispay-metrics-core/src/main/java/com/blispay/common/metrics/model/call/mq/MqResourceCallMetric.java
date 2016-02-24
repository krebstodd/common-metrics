package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class MqResourceCallMetric extends BaseResourceCallMetric<MqResourceCallEventData> {

    private final MqResourceCallEventData eventData;

    public MqResourceCallMetric(final ZonedDateTime timestamp,
                                final String applicationId,
                                final MetricGroup group,
                                final String name,
                                final MqResourceCallEventData eventData) {

        super(timestamp, applicationId, group, name);

        this.eventData = eventData;
    }

    @Override
    public MqResourceCallEventData eventData() {
        return this.eventData;
    }

}
