package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Enum HttpAction.
 */
public enum HttpAction implements Action {

    /**
     * Put.
     */
    PUT("PUT"),

    /**
     * Post.
     */
    POST("POST"),

    /**
     * Get.
     */
    GET("GET"),

    /**
     * Delete.
     */
    DELETE("DELETE"),

    /**
     * Options.
     */
    OPTIONS("OPTIONS"),

    /**
     * Head.
     */
    HEAD("HEAD"),

    /**
     * Trace.
     */
    TRACE("TRACE"),

    /**
     * Connect.
     */
    CONNECT("CONNECT");

    private static final Map<String, HttpAction> LOOKUP;

    static {
        final Map<String, HttpAction> temp = new HashMap<>();

        for (HttpAction action : HttpAction.values()) {
            temp.put(action.getValue(), action);
        }

        LOOKUP = Collections.unmodifiableMap(temp);
    }

    private final String action;

    /**
     * Constructs HttpAction.
     *
     * @param action action.
     */
    HttpAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return this.action;
    }

    /**
     * Method fromString.
     *
     * @param method method.
     * @return return value.
     */
    public static HttpAction fromString(final String method) {
        return LOOKUP.get(method.toUpperCase(Locale.ROOT));
    }

}
