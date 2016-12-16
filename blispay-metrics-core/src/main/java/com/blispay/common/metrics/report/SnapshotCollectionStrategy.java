package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

/**
 * Interface SnapshotCollectionStrategy.
 */
public interface SnapshotCollectionStrategy {

    /**
     * Method performCollection.
     *
     * @param snapshotProviders snapshotProviders.
     * @return return value.
     */
    Set<EventModel> performCollection(Collection<SnapshotProvider> snapshotProviders);

    /**
     * Method getTimeout.
     *
     * @return return value.
     */
    Duration getTimeout();

}
