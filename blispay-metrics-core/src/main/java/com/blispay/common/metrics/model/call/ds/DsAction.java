package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.call.Action;

/**
 * Enum DsAction.
 */
public enum DsAction implements Action {

    /**
     * Insert.
     */
    INSERT("INSERT"),

    /**
     * Update.
     */
    UPDATE("UPDATE"),

    /**
     * Delete.
     */
    DELETE("DELETE"),

    /**
     * Select.
     */
    SELECT("SELECT");

    private final String action;

    /**
     * Constructs DsAction.
     *
     * @param action action.
     */
    DsAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return action;
    }

}
