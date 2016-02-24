package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class HttpResourceCallMetric extends BaseResourceCallMetric<HttpResourceCallEventData> {

    private final HttpResourceCallEventData eventData;

    /**
     * Immutable http call response time metric.
     *
     * @param timestamp timestamp for when the metric occurred.
     * @param applicationId application name.
     * @param group metric group.
     * @param name metric name.
     * @param eventData summary of the http call and response.
     */
    public HttpResourceCallMetric(final ZonedDateTime timestamp,
                                  final String applicationId,
                                  final MetricGroup group,
                                  final String name,
                                  final HttpResourceCallEventData eventData) {

        super(timestamp, applicationId, group, name);

        this.eventData = eventData;
    }

    @Override
    public HttpResourceCallEventData eventData() {
        return this.eventData;
    }

}
