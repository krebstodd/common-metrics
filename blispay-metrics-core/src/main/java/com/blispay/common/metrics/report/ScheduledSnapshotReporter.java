package com.blispay.common.metrics.report;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScheduledSnapshotReporter extends SnapshotReporter {

    private final SnapshotScheduler scheduler;
    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    /**
     * Snapshot reporter that runs on a scheduled rate.
     *
     * @param scheduler Scheduler alerts reporter when to perform a snapshot.
     * @param snapshotCollectionStrategy Strategy for collecting snapshots.
     */
    public ScheduledSnapshotReporter(final SnapshotScheduler scheduler,
                                     final SnapshotCollectionStrategy snapshotCollectionStrategy) {

        super(snapshotCollectionStrategy);
        this.scheduler = scheduler;

        if (scheduler.getPeriod().compareTo(snapshotCollectionStrategy.getTimeout()) < 0) {
            logger().warn("Snapshot schedule period is greater than collection strategy timeout, could result in reporter thread contention.");
        }
    }

    protected abstract void handleScheduledSnapshot(final Snapshot snapshot);

    private void executeScheduledSnapshot() {
        handleScheduledSnapshot(super.report());
    }
    
    /**
     * Start scheduling the recurring reports.
     */
    @Override
    public void start() {

        logger().info("Starting scheduled snapshot reporter...");

        if (this.isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            this.scheduler.setListener(this::executeScheduledSnapshot);
            this.scheduler.start();

        } else {

            throw new IllegalStateException("Cannot start a running reporter.");

        }

        logger().info("Snapshot reporter started.");

    }

    /**
     * Stop scheduling future reports.
     */
    @Override
    public void stop() {

        logger().info("Stopping scheduled snapshot reporter...");

        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            this.scheduler.stop();

        } else {

            throw new IllegalStateException("Cannot stop a non-running reporter.");

        }

        logger().info("Scheduled snapshot reporter stopped.");

    }

    @Override
    public Boolean isRunning() {
        return this.isRunning.get();
    }

}
