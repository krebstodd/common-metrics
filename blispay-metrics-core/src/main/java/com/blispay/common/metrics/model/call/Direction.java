package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum Direction.
 */
public enum Direction {

    /**
     * Call is internal to a system (method call).
     */
    INTERNAL("INTERNAL"),

    /**
     * Call is coming into the resource (incoming http call for a server).
     */
    INBOUND("INBOUND"),

    /**
     * Call is leaving the resource (outbound http call to an third party api).
     */
    OUTBOUND("OUTBOUND");

    private final String direction;

    /**
     * Constructs Direction.
     *
     * @param direction direction.
     */
    Direction(final String direction) {
        this.direction = direction;
    }

    /**
     * Method getValue.
     *
     * @return return value.
     */
    @JsonValue
    public String getValue() {
        return this.direction;
    }

}
