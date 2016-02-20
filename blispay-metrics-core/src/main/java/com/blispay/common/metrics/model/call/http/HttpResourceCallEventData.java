package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Status;

public class HttpResourceCallEventData extends BaseResourceCallEventData<HttpAction, HttpResource> {

    public HttpResourceCallEventData(final Direction direction, final Long durationMillis, final HttpAction action, final HttpResource resource, final Status status) {
        super(direction, durationMillis, action, resource, status);
    }
}
