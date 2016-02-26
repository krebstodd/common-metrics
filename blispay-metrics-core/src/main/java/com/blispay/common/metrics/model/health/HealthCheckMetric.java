package com.blispay.common.metrics.model.health;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;

import java.time.ZonedDateTime;

public class HealthCheckMetric extends BaseMetricModel<HealthCheckData> {

    private static final MetricType type = MetricType.HEALTH_CHECK;

    private final HealthCheckData healthCheckData;

    /**
     * Immutable resource utilization metric.
     *
     * @param timestamp timestamp for when the metric occurred.
     * @param applicationId application name.
     * @param group metric group.
     * @param name metric name.
     * @param healthCheckData summary of health data.
     */
    public HealthCheckMetric(final ZonedDateTime timestamp,
                             final String applicationId,
                             final MetricGroup group,
                             final String name,
                             final HealthCheckData healthCheckData) {

        super(timestamp, applicationId, group, name, type);

        this.healthCheckData = healthCheckData;
    }

    @Override
    public HealthCheckData eventData() {
        return healthCheckData;
    }
}
