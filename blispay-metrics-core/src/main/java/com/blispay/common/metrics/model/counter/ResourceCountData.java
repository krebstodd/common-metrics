package com.blispay.common.metrics.model.counter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceCountData {

    @JsonProperty("count")
    private final Double count;

    public ResourceCountData(final Double count) {
        this.count = count;
    }

    public Double getCount() {
        return count;
    }

}
