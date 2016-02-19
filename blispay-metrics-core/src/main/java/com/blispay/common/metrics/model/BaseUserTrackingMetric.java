package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public abstract class BaseUserTrackingMetric<E> extends BaseMetricModel<E> {

    @JsonProperty("trackingInfo")
    private UserTrackingInfo trackingInfo;

    public BaseUserTrackingMetric(final ZonedDateTime timestamp,
                                  final MetricGroup group,
                                  final String name,
                                  final Type type) {

        super(timestamp, group, name, type);

        this.trackingInfo = trackingInfo;
    }

    public void setTrackingInfo(final UserTrackingInfo trackingInfo) {
        this.trackingInfo = trackingInfo;
    }

    public UserTrackingInfo getTrackingInfo() {
        return trackingInfo;
    }
}
