package com.blispay.common.metrics.model.call;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.BaseUserTrackingMetric;

import java.time.ZonedDateTime;

public abstract class BaseResourceCallMetric<D> extends BaseUserTrackingMetric<D> {

    private static final Type type = Type.RESOURCE_CALL;

    public BaseResourceCallMetric(final ZonedDateTime timestamp,
                                  final MetricGroup group,
                                  final String name) {

        super(timestamp, group, name, type);
    }

}
