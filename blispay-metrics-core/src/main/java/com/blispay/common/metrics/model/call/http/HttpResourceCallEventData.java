package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Status;

public class HttpResourceCallEventData extends BaseResourceCallEventData<HttpResource, HttpAction> {

    public HttpResourceCallEventData(final Direction direction, final Long durationMillis, final HttpResource resource, final HttpAction action, final Status status) {
        super(direction, durationMillis, resource, action, status);
    }
}
