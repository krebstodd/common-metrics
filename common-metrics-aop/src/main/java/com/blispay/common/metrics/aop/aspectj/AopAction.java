package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.model.call.Action;

/**
 * Metric action type representing the target of an AOP proxy.
 */
public final class AopAction implements Action {

    private final String value;

    private AopAction(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * Create a new aop action with the provided name.
     * @param actionName Action name.
     * @return Aop action.
     */
    public static AopAction withName(final String actionName) {
        return new AopAction(actionName);
    }
}
