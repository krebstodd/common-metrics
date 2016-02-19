package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Action;

public enum HttpAction implements Action {

    PUT("PUT"),
    POST("POST"),
    GET("GET"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private final String action;

    private HttpAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return null;
    }

}
