package com.blispay.common.metrics.model.health;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.MetricGroup;

public class HealthCheckMetricFactory extends BaseMetricFactory<HealthCheckMetric, HealthCheckData>{

    public HealthCheckMetricFactory(final String application, final MetricGroup group, final String name) {
        super(application, group, name);
    }

    public HealthCheckMetric newMetric(final HealthCheckData eventData) {
        return new HealthCheckMetric(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }

}
