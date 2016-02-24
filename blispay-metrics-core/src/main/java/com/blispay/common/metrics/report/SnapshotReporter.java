package com.blispay.common.metrics.report;

import java.util.Set;
import java.util.function.Supplier;

public interface SnapshotReporter {

    Snapshot report();

    void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers);

    default void start() {

    }

    default void stop() {

    }

}
