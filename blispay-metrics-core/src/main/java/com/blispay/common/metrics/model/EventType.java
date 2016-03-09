package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {

    /**
     * A resource call, execution time.
     */
    TRANSACTION("TX"),
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
    EVENT("EVENT"),
    /**
     * Resource status.
     */
    STATUS("STATUS");

    private final String type;

    private EventType(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getValue() {
        return type;
    }

}