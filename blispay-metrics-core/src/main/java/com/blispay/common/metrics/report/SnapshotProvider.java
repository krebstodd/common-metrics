package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.util.concurrent.Callable;

/**
 * Interface SnapshotProvider.
 */
public interface SnapshotProvider extends Callable<EventModel> {

    /**
     * Method snapshot.
     *
     * @return return value.
     */
    EventModel snapshot();

    /**
     * Method call.
     *
     * @return return value.
     */
    default EventModel call() {
        return snapshot();
    }

}
