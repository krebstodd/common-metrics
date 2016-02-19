package com.blispay.common.metrics.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class BaseMetricFactory<R extends BaseMetricModel<D>, D> {

    private final MetricGroup group;
    private final String name;

    public BaseMetricFactory(final MetricGroup group, final String name) {
        this.group = group;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MetricGroup getGroup() {
        return group;
    }

    public static ZonedDateTime timestamp() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public abstract R newMetric(final D eventData);

}
