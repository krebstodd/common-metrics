package com.blispay.common.metrics.metric;

import java.util.LinkedList;
import java.util.List;

public class InfrastructureMetricName extends MetricName {

    private final String applicationId;

    private final String componentId;

    private final String subComponentId;

    private final String attributeId;

    public InfrastructureMetricName(final String applicationId, final String componentId, final String attributeId) {
        this.applicationId = applicationId;
        this.componentId = componentId;
        this.subComponentId = null;
        this.attributeId = attributeId;
    }

    public InfrastructureMetricName(final String applicationId, final String componentId, final String subComponentId, final String attributeId) {
        this.applicationId = applicationId;
        this.componentId = componentId;
        this.subComponentId = subComponentId;
        this.attributeId = attributeId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getSubComponentId() {
        return subComponentId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    @Override
    public List<String> nameComponents() {
        final List<String> components = new LinkedList<>();

        components.add(applicationId);
        components.add(componentId);

        if (subComponentId != null) {
            components.add(subComponentId);
        }

        components.add(attributeId);

        return components;
    }

}
