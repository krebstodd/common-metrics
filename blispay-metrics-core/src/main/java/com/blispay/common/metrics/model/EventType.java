package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum EventType.
 */
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

    /**
     * Constructs EventType.
     *
     * @param type type.
     */
    EventType(final String type) {
        this.type = type;
    }

    /**
     * Method getValue.
     *
     * @return return value.
     */
    @JsonValue
    public String getValue() {
        return type;
    }

}
