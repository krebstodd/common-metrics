package com.blispay.common.metrics.model.counter;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;

import java.time.ZonedDateTime;

public class ResourceCounterMetric extends BaseMetricModel<ResourceCounterEventData> {

    private static final Type type = Type.RESOURCE_COUNTER;

    private ResourceCounterEventData eventData;

    public ResourceCounterMetric(final ZonedDateTime timestamp,
                                 final MetricGroup group,
                                 final String name,
                                 final ResourceCounterEventData eventData) {

        super(timestamp, group, name, type);
        this.eventData = eventData;
    }

    public ResourceCounterEventData eventData() {
        return eventData;
    }
}
