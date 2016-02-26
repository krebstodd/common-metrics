package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MqResourceCallEventData extends BaseResourceCallEventData<MqResource, MqAction> {

    @JsonProperty("requestQueue")
    private final String requestQueue;

    @JsonProperty("responseQueue")
    private final String responseQueue;

    @JsonProperty("host")
    private final String host;

    @JsonProperty("requestType")
    private final String requestType;

    public MqResourceCallEventData(final Direction direction, final Long durationMillis,
                                   final MqResource resource, final MqAction action,
                                   final Status status, final TrackingInfo trackingInfo,
                                   final String requestQueue, final String responseQueue,
                                   final String host, final String requestType) {

        super(direction, durationMillis, resource, action, status, trackingInfo);

        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.host = host;
        this.requestType = requestType;
    }

    public String getRequestQueue() {
        return requestQueue;
    }

    public String getResponseQueue() {
        return responseQueue;
    }

    public String getHost() {
        return host;
    }

    public String getRequestType() {
        return requestType;
    }
}
