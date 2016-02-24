package com.blispay.common.metrics.model.business;

import com.blispay.common.metrics.model.MetricGroup;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventFactory {

    private final String application;
    private final MetricGroup group;
    private final String name;

    public EventFactory(final String application, final MetricGroup group, final String name) {
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

    private static ZonedDateTime timestamp() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public <T> EventMetric<T> newMetric(final T eventData) {
        return new EventMetric<>(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }

}
