package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;

public class NoOpEventEmitter implements EventEmitter {

    @Override
    public void emit(final EventModel event) {

    }

}
