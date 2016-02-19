package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

public class MqResourceCallEventData extends BaseResourceCallEventData {

    public MqResourceCallEventData(final Direction direction,
                                   final Long durationMillis,
                                   final MqAction action,
                                   final MqResource resource,
                                   final Status status) {

        super(direction, durationMillis, action, resource, status);
    }

}
