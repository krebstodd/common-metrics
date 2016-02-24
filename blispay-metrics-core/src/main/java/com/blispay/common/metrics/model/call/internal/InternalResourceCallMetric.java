package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class InternalResourceCallMetric extends BaseResourceCallMetric<InternalResourceCallEventData> {

    private final InternalResourceCallEventData eventData;

    /**
     * Immutable resource call metric, used to profile method calls internal to an app.
     *
     * @param timestamp timestamp for when the call occurred.
     * @param applicationId application name.
     * @param group metric group.
     * @param name metric name.
     * @param eventData summary of resource call.
     */
    public InternalResourceCallMetric(final ZonedDateTime timestamp,
                                      final String applicationId,
                                      final MetricGroup group,
                                      final String name,
                                      final InternalResourceCallEventData eventData) {

        super(timestamp, applicationId, group, name);

        this.eventData = eventData;
    }

    @Override
    public InternalResourceCallEventData eventData() {
        return this.eventData;
    }

}
