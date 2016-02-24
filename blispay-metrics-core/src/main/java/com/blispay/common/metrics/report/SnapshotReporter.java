package com.blispay.common.metrics.report;

import java.util.Set;
import java.util.function.Supplier;

public interface SnapshotReporter {

    public abstract Snapshot report();

    public abstract void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers);

    public default void start() {

    }

    public default void stop() {

    }

}
