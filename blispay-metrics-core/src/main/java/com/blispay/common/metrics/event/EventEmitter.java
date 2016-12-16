package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;

/**
 * Interface EventEmitter.
 */
public interface EventEmitter {

    /**
     * Method emit.
     *
     * @param event event.
     */
    void emit(EventModel event);

}
