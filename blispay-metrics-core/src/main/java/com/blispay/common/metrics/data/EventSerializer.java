package com.blispay.common.metrics.data;

import com.blispay.common.metrics.model.EventModel;

/**
 * Interface EventSerializer.
 */
public interface EventSerializer {

    /**
     * Method serialize.
     *
     * @param metric metric.
     * @return return value.
     */
    String serialize(EventModel metric);

}
