package com.blispay.common.metrics.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class BaseMetricFactory<R extends BaseMetricModel<D>, D> {

    private final String application;
    private final MetricGroup group;
    private final String name;

    public BaseMetricFactory(final String application, final MetricGroup group, final String name) {
        this.application = application;
        this.group = group;
        this.name = name;
    }

    public String getApplication() {
        return application;
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
