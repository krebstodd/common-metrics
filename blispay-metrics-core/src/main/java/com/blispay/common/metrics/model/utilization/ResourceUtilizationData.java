package com.blispay.common.metrics.model.utilization;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceUtilizationData {

    @JsonProperty("min")
    private final Long minValue;

    @JsonProperty("max")
    private final Long maxValue;

    @JsonProperty("curr")
    private final Long currentValue;

    @JsonProperty("util")
    private final Double currentPercentage;

    /**
     * Generic summary of resource utilization.
     *
     * @param minValue Minimum utilization level of resource.
     * @param maxValue Maximum utilization level of resource.
     * @param currentValue The current utilization level.
     * @param currentPercentage Percentage of maximum currently under use.
     */
    public ResourceUtilizationData(final Long minValue,
                                   final Long maxValue,
                                   final Long currentValue,
                                   final Double currentPercentage) {

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        this.currentPercentage = currentPercentage;
    }

    public Long getMinValue() {
        return minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public Double getCurrentPercentage() {
        return currentPercentage;
    }
}
