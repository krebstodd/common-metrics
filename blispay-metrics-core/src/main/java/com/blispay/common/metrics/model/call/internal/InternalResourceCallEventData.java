package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

public class InternalResourceCallEventData extends BaseResourceCallEventData<InternalResource, InternalAction> {

    public InternalResourceCallEventData(final Direction direction, final Long durationMillis, final InternalResource resource, final InternalAction action, final Status status, final UserTrackingInfo trackingInfo) {
        super(direction, durationMillis, resource, action, status, trackingInfo);
    }

}
