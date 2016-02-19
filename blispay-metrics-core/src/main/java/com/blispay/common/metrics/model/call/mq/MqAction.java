package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.call.Action;

public enum MqAction implements Action {

    PUT("PUT"),
    GET("GET"),
    PEEK("PEEK");

    private final String action;

    private MqAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return null;
    }

}