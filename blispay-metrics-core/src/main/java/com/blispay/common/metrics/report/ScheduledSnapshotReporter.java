package com.blispay.common.metrics.report;

import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScheduledSnapshotReporter implements SnapshotReporter {

    private final ScheduledExecutorService executorService;

    private final Integer period;

    private final TimeUnit unit;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Create a new scheduled reporter. Subclass must implement a report method that will be called periodically.
     *
     * @param period The period between calls to report method.
     * @param unit The time unit of the period argument.
     */
    public ScheduledSnapshotReporter(final Integer period, final TimeUnit unit) {
        this.period = period;
        this.unit = unit;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    protected abstract Logger getLogger();

    /**
     * Start scheduling the recurring reports.
     */
    public void start() {

        getLogger().info("Starting scheduled snapshot reporter with period [{}] units [{}]...", period, unit);

        if (isRunning.compareAndSet(false, true)) {
            this.executorService.scheduleAtFixedRate(this::doReport , period, period, unit);
        }

        getLogger().info("Snapshot reporter started.");

    }

    private void doReport() {
        try {

            getLogger().info("Starting timed slf4j metrics report...");
            this.report();
            getLogger().info("Timed slf4j metrics report complete.");

        // CHECK_OFF: IllegalCatch
        } catch (Throwable throwable) {
            getLogger().error("Caught exception attempting to run scheduled gauge report...", throwable);
        }
        // CHECK_ON: IllegalCatch

    }

    /**
     * Stop scheduling future reports.
     */
    public void stop() {

        getLogger().info("Stopping scheduled snapshot reporter...");

        this.executorService.shutdown();

        try {
            if (!this.executorService.awaitTermination(1L, TimeUnit.SECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            getLogger().error("There was an error gracefully shutting down snapshot reporter, initiating hard shut down.");
            this.executorService.shutdownNow();
        } finally {
            getLogger().info("Scheduled snapshot reporter stopped.");
        }
    }

    public Boolean isRunning() {
        return isRunning.get();
    }
}
