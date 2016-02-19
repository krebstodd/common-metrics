package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Direction {

    INTERNAL("INTERNAL"),
    INBOUND("INBOUND"),
    OUTBOUND("OUTBOUND");

    private final String direction;

    private Direction(final String direction) {
        this.direction = direction;
    }

    @JsonValue
    public String getValue() {
        return this.direction;
    }

}
