package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents what happening as a result of the transaction. For example, the action for a database query might be SELECT
 * and the action for an api call might be POST.
 */
public interface Action {

    /**
     * Method getValue.
     *
     * @return return value.
     */
    @JsonValue
    String getValue();

    /**
     * Create a new action with the provided name.
     * @param actionName Action name.
     * @return Action instance.
     */
    static Action withName(final String actionName) {
        return new BasicAction(actionName);
    }

}
