package com.blispay.common.metrics.model;

public enum MetricGroup {

    EH_CACHE("metrics.resources.ehcache");

    private final String groupName;

    private MetricGroup(final String groupName) {
        this.groupName = groupName;
    }

    public String getValue() {
        return groupName;
    }
}
