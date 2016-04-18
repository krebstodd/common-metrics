package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.util.concurrent.Callable;

public interface SnapshotProvider extends Callable<EventModel> {

    EventModel snapshot();

    default EventModel call() {
        return snapshot();
    }

}
