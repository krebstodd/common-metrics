package com.blispay.common.metrics.model.utilization;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceUtilizationData {

    @JsonProperty("minValue")
    private final Long minValue;

    @JsonProperty("maxValue")
    private final Long maxValue;

    @JsonProperty("currentValue")
    private final Long currentValue;

    @JsonProperty("currentPercentage")
    private final Double currentPercentage;

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
