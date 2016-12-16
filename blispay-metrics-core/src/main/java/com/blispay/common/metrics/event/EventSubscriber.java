package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Interface EventSubscriber.
 */
public interface EventSubscriber {

    /**
     * Method getFilters.
     *
     * @return return value.
     */
    default Collection<EventFilter> getFilters() {
        return new LinkedList<>();
    }

    /**
     * Method acceptEvent.
     *
     * @param event event.
     */
    void acceptEvent(EventModel event);

}
