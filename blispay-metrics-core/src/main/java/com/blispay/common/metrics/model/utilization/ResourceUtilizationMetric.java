package com.blispay.common.metrics.model.utilization;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;

import java.time.ZonedDateTime;

public class ResourceUtilizationMetric extends BaseMetricModel<ResourceUtilizationData> {

    private static final MetricType type = MetricType.RESOURCE_UTILIZATION;

    private final ResourceUtilizationData utilizationEventData;

    public ResourceUtilizationMetric(final ZonedDateTime timestamp,
                                     final String applicationId,
                                     final MetricGroup group,
                                     final String name,
                                     final ResourceUtilizationData utilizationSummary) {

        super(timestamp, applicationId, group, name, type);

        this.utilizationEventData = utilizationSummary;
    }

    @Override
    public ResourceUtilizationData eventData() {
        return utilizationEventData;
    }
}
