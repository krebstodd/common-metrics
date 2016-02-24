package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum HttpAction implements Action {

    PUT("PUT"),
    POST("POST"),
    GET("GET"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private static final Map<String, HttpAction> lookup;

    static {
        final Map<String, HttpAction> temp = new HashMap<>();

        for (HttpAction action : HttpAction.values()) {
            temp.put(action.getValue(), action);
        }

        lookup = Collections.unmodifiableMap(temp);
    }

    private final String action;

    private HttpAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return this.action;
    }

    public static HttpAction fromString(final String method) {
        return lookup.get(method.toUpperCase(Locale.ROOT));
    }

}
