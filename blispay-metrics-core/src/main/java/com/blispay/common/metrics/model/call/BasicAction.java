package com.blispay.common.metrics.model.call;

/**
 * Basic action impl.
 */
class BasicAction implements Action {

    private final String value;

    BasicAction(final String actionName) {
        this.value = actionName;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
