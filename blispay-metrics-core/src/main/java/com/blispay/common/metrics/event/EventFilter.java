package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;

/**
 * Interface EventFilter.
 */
public interface EventFilter {

    /**
     * Method acceptsEvent.
     *
     * @param event event.
     * @return return value.
     */
    Boolean acceptsEvent(EventModel event);

}
