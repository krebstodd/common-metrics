package com.blispay.common.metrics.model.call;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;

import java.time.ZonedDateTime;

public abstract class BaseResourceCallMetric<D> extends BaseMetricModel<D> {

    private static final MetricType type = MetricType.RESOURCE_CALL;

    public BaseResourceCallMetric(final ZonedDateTime timestamp,
                                  final String applicationId,
                                  final MetricGroup group,
                                  final String name) {

        super(timestamp, applicationId, group, name, type);
    }

}
