package com.blispay.common.metrics.report;

import org.slf4j.Logger;

public abstract class SnapshotReporter {

    private final SnapshotCollectionStrategy snapshotCollector;

    private SnapshotProviderSet snapshotProviderSet;

    public SnapshotReporter(final SnapshotCollectionStrategy snapshotCollector) {
        this.snapshotCollector = snapshotCollector;
    }

    public abstract void start();

    public abstract void stop();

    public abstract Boolean isRunning();

    public abstract Logger logger();

    /**
     * Perform a report.
     * @return Snapshot of the current provider set.
     */
    public Snapshot report() {

        if (snapshotProviderSet == null) {
            throw new IllegalStateException("Snapshot provider set not initialized.");
        }

        return new Snapshot(snapshotCollector.performCollection(snapshotProviderSet.getAll()));

    }

    public void setSnapshotProviders(final SnapshotProviderSet providerSet) {
        this.snapshotProviderSet = providerSet;
    }

}
