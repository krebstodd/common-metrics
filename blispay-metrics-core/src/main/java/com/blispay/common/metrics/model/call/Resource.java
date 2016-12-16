package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Class Resource.
 */
public abstract class Resource {

    /**
     * Method getValue.
     *
     * @return return value.
     */
    @JsonValue
    public abstract String getValue();

}
