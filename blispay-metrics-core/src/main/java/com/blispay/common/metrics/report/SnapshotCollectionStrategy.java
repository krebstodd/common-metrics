package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

public interface SnapshotCollectionStrategy {

    Set<EventModel> performCollection(Collection<SnapshotProvider> snapshotProviders);

    Duration getTimeout();

}
