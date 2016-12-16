package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.call.Action;

/**
 * Class InternalAction.
 */
public final class InternalAction implements Action {

    private final String action;

    /**
     * Constructs InternalAction.
     *
     * @param action action.
     */
    private InternalAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return action;
    }

    /**
     * Method fromMethodName.
     *
     * @param methodName methodName.
     * @return return value.
     */
    public static InternalAction fromMethodName(final String methodName) {
        return new InternalAction(methodName);
    }

}
