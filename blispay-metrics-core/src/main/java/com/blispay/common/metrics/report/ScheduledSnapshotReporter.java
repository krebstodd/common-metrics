package com.blispay.common.metrics.report;

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

    /**
     * Start scheduling the recurring reports.
     */
    public void start() {

        if (isRunning.compareAndSet(false, true)) {
            this.executorService.scheduleAtFixedRate(this::report , period, period, unit);
        }

    }

    /**
     * Stop scheduling future reports.
     */
    public void stop() {
        this.executorService.shutdown();

        try {
            if (!this.executorService.awaitTermination(1L, TimeUnit.SECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
        }
    }

    public Boolean isRunning() {
        return isRunning.get();
    }
}
