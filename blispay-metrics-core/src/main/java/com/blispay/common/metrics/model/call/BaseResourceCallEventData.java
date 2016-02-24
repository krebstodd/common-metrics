package com.blispay.common.metrics.model.call;

import com.blispay.common.metrics.model.UserTrackingInfo;
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

    @JsonProperty("trackingInfo")
    private UserTrackingInfo trackingInfo;

    public BaseResourceCallEventData(final Direction direction, final Long durationMillis,
                                     final R resource, final A action, final Status status,
                                     final UserTrackingInfo trackingInfo) {

        this.direction = direction;
        this.durationMillis = durationMillis;
        this.action = action;
        this.resource = resource;
        this.status = status.getValue();
        this.trackingInfo = trackingInfo;
    }

    public Direction getDirection() {
        return direction;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public A getAction() {
        return action;
    }

    public R getResource() {
        return resource;
    }

    public Integer getStatus() {
        return status;
    }

    public UserTrackingInfo getTrackingInfo() {
        return trackingInfo;
    }


}
