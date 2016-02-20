package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

public class DataSourceResourceCallEventData extends BaseResourceCallEventData<DsResource, DsAction> {
    public DataSourceResourceCallEventData(final Direction direction, final Long durationMillis, final DsAction resource, final DsResource action, final Status status) {
        super(direction, durationMillis, resource, action, status);
    }
}
