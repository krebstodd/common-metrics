package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.call.Action;

/**
 * Enum MqAction.
 */
public enum MqAction implements Action {

    /**
     * Put action.
     */
    PUT("PUT"),

    /**
     * Get action.
     */
    GET("GET"),

    /**
     * Peek action.
     */
    PEEK("PEEK");

    private final String action;

    /**
     * Constructs MqAction.
     *
     * @param action action.
     */
    MqAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return this.action;
    }

}
