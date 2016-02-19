package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.call.Action;

public enum DsAction implements Action {

    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
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