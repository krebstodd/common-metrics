package com.blispay.common.metrics.model.business;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.UserTrackingInfo;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventFactory {

    private final MetricGroup group;
    private final String name;

    public EventFactory(final MetricGroup group, final String name) {
        this.group = group;
        this.name = name;
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

    public <T> EventMetric<T> newMetric(final UserTrackingInfo trackingInfo, final T eventData) {
        return new EventMetric<>(timestamp(), getGroup(), getName(), trackingInfo, eventData);
    }

}
