package com.blispay.common.metrics.model.counter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class ResourceCountData.
 */
public class ResourceCountData {

    @JsonProperty("count")
    private final Double count;

    /**
     * Constructs ResourceCountData.
     *
     * @param count count.
     */
    public ResourceCountData(final Double count) {
        this.count = count;
    }

    /**
     * Method getCount.
     *
     * @return return value.
     */
    public Double getCount() {
        return count;
    }

}
