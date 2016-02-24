package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class HttpResourceCallMetric extends BaseResourceCallMetric<HttpResourceCallEventData> {

    private final HttpResourceCallEventData eventData;

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
