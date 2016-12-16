package com.blispay.common.metrics.report;

import org.slf4j.Logger;

/**
 * Class SnapshotReporter.
 */
public abstract class SnapshotReporter {

    private final SnapshotCollectionStrategy snapshotCollector;

    private SnapshotProviderSet snapshotProviderSet;

    /**
     * Constructs SnapshotReporter.
     *
     * @param snapshotCollector snapshotCollector.
     */
    public SnapshotReporter(final SnapshotCollectionStrategy snapshotCollector) {
        this.snapshotCollector = snapshotCollector;
    }

    /**
     * Method start.
     *
     */
    public abstract void start();

    /**
     * Method stop.
     *
     */
    public abstract void stop();

    /**
     * Method isRunning.
     *
     * @return return value.
     */
    public abstract Boolean isRunning();

    /**
     * Method logger.
     *
     * @return return value.
     */
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

    /**
     * Method setSnapshotProviders.
     *
     * @param providerSet providerSet.
     */
    public void setSnapshotProviders(final SnapshotProviderSet providerSet) {
        this.snapshotProviderSet = providerSet;
    }

}
