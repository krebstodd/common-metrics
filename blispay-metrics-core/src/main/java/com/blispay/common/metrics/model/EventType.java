package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {

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
    RESOURCE_COUNT("CNT"),
    /**
     * Event occurrence.
     */
    BUSINESS_EVT("EVENT"),
    /**
     * Infrastructure event.
     */
    INFRA_EVT("INFRA_EVT"),
    /**
     * Health check.
     */
    HEALTH_CHECK("HLTH");

    private final String type;

    private EventType(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getValue() {
        return type;
    }

}