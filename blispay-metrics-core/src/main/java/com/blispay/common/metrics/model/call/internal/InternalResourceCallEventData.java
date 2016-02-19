package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

public class InternalResourceCallEventData extends BaseResourceCallEventData {

    public InternalResourceCallEventData(final Direction direction,
                                         final Long durationMillis,
                                         final InternalAction action,
                                         final InternalResource resource,
                                         final Status status) {

        super(direction, durationMillis, action, resource, status);
    }

}
