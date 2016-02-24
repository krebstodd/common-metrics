package com.blispay.common.metrics.model.counter;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;

import java.time.ZonedDateTime;

public class ResourceCounterMetric extends BaseMetricModel<ResourceCounterEventData> {

    private static final MetricType type = MetricType.RESOURCE_COUNTER;

    private ResourceCounterEventData eventData;

    public ResourceCounterMetric(final ZonedDateTime timestamp,
                                 final String applicationId,
                                 final MetricGroup group,
                                 final String name,
                                 final ResourceCounterEventData eventData) {

        super(timestamp, applicationId, group, name, type);
        this.eventData = eventData;
    }

    public ResourceCounterEventData eventData() {
        return eventData;
    }
}
