package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;

public interface EventEmitter {

    void emit(final EventModel event);

}
