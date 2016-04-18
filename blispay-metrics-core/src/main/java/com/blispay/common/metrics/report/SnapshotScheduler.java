package com.blispay.common.metrics.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SnapshotScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(SnapshotScheduler.class);

    private final Long period;
    private final Long initialDelay;
    private final TimeUnit timeUnit;

    private final Strategy strategy;
    private final ScheduledExecutorService executorService;

    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    private NotificationListener notificationListener;

    private SnapshotScheduler(final Duration period,
                              final Duration initialDelay,
                              final Strategy strategy) {

        this.period = period.toNanos();
        this.initialDelay = initialDelay.toNanos();
        this.timeUnit = TimeUnit.NANOSECONDS;

        this.strategy = strategy;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Start this scheduler. Requires that the scheduler is not currently running and that a notification listener has been provided.
     */
    public void start() {

        LOG.info("Attempting to start metric snapshot scheduler...");

        if (this.notificationListener == null) {
            throw new IllegalStateException("Cannot start scheduler without first setting listener.");
        }

        if (this.isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            if (this.strategy == Strategy.FIXED_RATE) {
                this.executorService.scheduleAtFixedRate(this.notificationListener::doNotify, this.initialDelay, this.period, this.timeUnit);
            } else {
                this.executorService.scheduleWithFixedDelay(this.notificationListener::doNotify, this.initialDelay, this.period, this.timeUnit);
            }

        } else {

            throw new IllegalStateException("Cannot start already running metric snapshot scheduler.");

        }

        LOG.info("Metric snapshot scheduler started with strategy [{}], initial delay [{}], period [{}], units [{}]", strategy, initialDelay, period, timeUnit);

    }

    /**
     * Stop the currently running scheduler.
     */
    public void stop() {

        LOG.info("Stopping metric snapshot scheduler...");

        if (this.isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            this.executorService.shutdown();

        }

        LOG.info("Metric snapshot scheduler stopped...");

    }

    public Boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Set the notification listener on this object. This must be set before starting the scheduler.
     *
     * @param listener Notification listener.
     */
    public synchronized void setListener(final NotificationListener listener) {

        if (this.isRunning.get()) {
            throw new IllegalStateException("Cannot set notification listener after scheduler has started.");
        }

        this.notificationListener = listener;

    }

    public Duration getPeriod() {
        return Duration.ofMillis(this.period);
    }

    public static SnapshotScheduler scheduleFixedRate(final Duration period) {
        return new SnapshotScheduler(period, period, Strategy.FIXED_RATE);
    }
    
    public static SnapshotScheduler scheduleFixedRateWithInitialDelay(final Duration period, final Duration delay) {
        return new SnapshotScheduler(period, delay, Strategy.FIXED_RATE);
    }

    public static SnapshotScheduler scheduleFixedDelay(final Duration period) {
        return new SnapshotScheduler(period, period, Strategy.FIXED_DELAY);
    }

    public static SnapshotScheduler scheduleFixedDelayWithInitialDelay(final Duration period, final Duration delay) {
        return new SnapshotScheduler(period, delay, Strategy.FIXED_DELAY);
    }

    private static enum Strategy {

        FIXED_RATE,
        FIXED_DELAY;

    }

    @FunctionalInterface
    public interface NotificationListener {
        void doNotify();
    }

}
