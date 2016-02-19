package com.blispay.common.metrics.metric;

public enum MetricType {

    HEALTH("HLTH"),
    PERFORMANCE("PRF"),
    RESOURCE_UTILIZATION("UTL"),
    COUNT("CNT");

    private final String value;

    private MetricType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}