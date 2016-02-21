package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public abstract class BaseUserTrackingMetric<D> extends BaseMetricModel<D> {

    @JsonProperty("trackingInfo")
    private UserTrackingInfo trackingInfo;

    public BaseUserTrackingMetric(final ZonedDateTime timestamp,
                                  final MetricGroup group,
                                  final String name,
                                  final MetricType type) {

        super(timestamp, group, name, type);
    }

    public void setTrackingInfo(final UserTrackingInfo trackingInfo) {
        this.trackingInfo = trackingInfo;
    }

    public UserTrackingInfo getTrackingInfo() {
        return trackingInfo;
    }
}
