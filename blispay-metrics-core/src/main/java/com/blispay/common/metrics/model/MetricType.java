package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetricType {

    RESOURCE_CALL("CAL"),
    RESOURCE_UTILIZATION("UTL"),
    RESOURCE_COUNTER("CNT"),
    EVENT("EVENT");

    private final String type;

    private MetricType(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getValue() {
        return type;
    }

}