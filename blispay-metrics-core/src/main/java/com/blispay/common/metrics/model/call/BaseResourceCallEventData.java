package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseResourceCallEventData<R extends Resource, A extends Action> {

    @JsonProperty("direction")
    private final Direction direction;

    @JsonProperty("durationMillis")
    private final Long durationMillis;

    @JsonProperty("action")
    private final A action;

    @JsonProperty("resource")
    private final R resource;

    @JsonProperty("status")
    private final Integer status;

    public BaseResourceCallEventData(final Direction direction, final Long durationMillis,
                                     final A action, final R resource, final Status status) {
        this.direction = direction;
        this.durationMillis = durationMillis;
        this.action = action;
        this.resource = resource;
        this.status = status.getValue();
    }

}
