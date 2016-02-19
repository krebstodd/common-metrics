package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseResourceCallEventData {

    @JsonProperty("direction")
    private final Direction direction;

    @JsonProperty("durationMillis")
    private final Long durationMillis;

    @JsonProperty("action")
    private final Action action;

    @JsonProperty("resource")
    private final Resource resource;

    @JsonProperty("status")
    private final Integer status;

    public BaseResourceCallEventData(final Direction direction, final Long durationMillis, final Action action, final Resource resource, final Status status) {
        this.direction = direction;
        this.durationMillis = durationMillis;
        this.action = action;
        this.resource = resource;
        this.status = status.getValue();
    }

}
