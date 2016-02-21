package com.blispay.common.metrics.model;

public class InfraMetricName extends MetricName {

    public InfraMetricName(final String appId, final String component, final String entity) {
        this(appId, component, null, entity);
    }

    public InfraMetricName(final String appId, final String component, final String subComponent, final String entity) {

        addComponent(appId);
        addComponent(component);

        if (subComponent != null) {
            addComponent(subComponent);
        }

        addComponent(entity);

    }

}
