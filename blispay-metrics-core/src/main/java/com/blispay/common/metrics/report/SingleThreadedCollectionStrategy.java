package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SingleThreadedCollectionStrategy implements SnapshotCollectionStrategy {
    @Override
    public Set<EventModel> performCollection(final Collection<SnapshotProvider> snapshotProviders) {
        return snapshotProviders.stream().map(SnapshotProvider::snapshot).collect(Collectors.toSet());
    }

    @Override
    public Duration getTimeout() {
        return Duration.ofMillis(Integer.MAX_VALUE);
    }

}
