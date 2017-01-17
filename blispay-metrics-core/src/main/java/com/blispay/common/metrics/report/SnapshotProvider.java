package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

/**
 * Interface SnapshotProvider.
 */
public interface SnapshotProvider {

    /**
     * Method snapshot.
     *
     * @return return value.
     */
    EventModel snapshot();

    /**
     * Get a unique identifier that describes the snapshot provider.
     * @return Identifier.
     */
    default String id() {
        return getClass().getCanonicalName();
    }

    /**
     * String that describes what the snapshot provider is taking snapshots of.
     * @return Description string.
     */
    default String description() {
        return "Snapshot provider " + getClass().getSimpleName();
    }

}
