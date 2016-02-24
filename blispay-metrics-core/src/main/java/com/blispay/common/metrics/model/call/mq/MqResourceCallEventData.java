package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

public class MqResourceCallEventData extends BaseResourceCallEventData<MqResource, MqAction> {

    public MqResourceCallEventData(final Direction direction, final Long durationMillis, final MqResource resource, final MqAction action, final Status status, final UserTrackingInfo trackingInfo) {
        super(direction, durationMillis, resource, action, status, trackingInfo);
    }

}
