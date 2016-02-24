package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.call.Action;

public final class InternalAction implements Action {

    private final String action;

    private InternalAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return action;
    }

    public static InternalAction fromMethodName(final String methodName) {
        return new InternalAction(methodName);
    }

}