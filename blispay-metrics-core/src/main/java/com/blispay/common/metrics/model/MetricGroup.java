package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetricGroup {

    GENERIC("metrics.generic"),
    EH_CACHE("metrics.resources.ehcache");

    private final String groupName;

    private MetricGroup(final String groupName) {
        this.groupName = groupName;
    }

    @JsonValue
    public String getValue() {
        return groupName;
    }
}
