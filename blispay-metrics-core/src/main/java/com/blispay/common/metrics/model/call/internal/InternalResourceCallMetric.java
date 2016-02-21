package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.InfraMetricName;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class InternalResourceCallMetric extends BaseResourceCallMetric<InternalResourceCallEventData> {

    private final InternalResourceCallEventData eventData;

    public InternalResourceCallMetric(final ZonedDateTime timestamp,
                                      final MetricGroup group,
                                      final String name,
                                      final InternalResourceCallEventData eventData) {

        super(timestamp, group, name);

        this.eventData = eventData;
    }

    @Override
    public InternalResourceCallEventData eventData() {
        return this.eventData;
    }

}
