package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

public interface Action {

    @JsonValue
    public String getValue();

}
