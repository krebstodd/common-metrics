package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.MetricGroup;

public class HttpResourceCallMetricFactory extends BaseMetricFactory<HttpResourceCallMetric, HttpResourceCallEventData> {

    public HttpResourceCallMetricFactory(final String application, final MetricGroup group, final String name) {
        super(application, group, name);
    }

    public HttpResourceCallMetric newMetric(final HttpResourceCallEventData eventData) {
        return new HttpResourceCallMetric(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }

}
