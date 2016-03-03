package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;

public interface EventFilter {

    Boolean acceptsEvent(EventModel event);

}