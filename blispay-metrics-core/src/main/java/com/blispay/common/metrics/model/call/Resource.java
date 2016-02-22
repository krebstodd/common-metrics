package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract class Resource {

    @JsonValue
    public abstract String getValue();

}
