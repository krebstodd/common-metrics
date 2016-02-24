package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetricType {

    /**
     * A resource call, execution time.
     */
    RESOURCE_CALL("CAL"),
    /**
     * Resource utilization level.
     */
    RESOURCE_UTILIZATION("UTL"),
    /**
     * Resource counter.
     */
    RESOURCE_COUNTER("CNT"),
    /**
     * Event occurrence.
     */
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