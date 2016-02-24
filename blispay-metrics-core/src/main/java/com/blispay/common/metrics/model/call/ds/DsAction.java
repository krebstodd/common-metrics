package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.call.Action;

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

    private DsAction(final String action) {
        this.action = action;
    }

    @Override
    public String getValue() {
        return action;
    }

}