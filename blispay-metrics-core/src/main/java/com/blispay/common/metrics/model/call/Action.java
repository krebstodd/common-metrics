package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Interface Action.
 */
public interface Action {

    /**
     * Method getValue.
     *
     * @return return value.
     */
    @JsonValue
    String getValue();

}
