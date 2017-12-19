package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A no-op transaction will not publish any metric events to the {@link com.blispay.common.metrics.MetricService}.
 * No-op transactions are useful for cases in which you want to provide a transaction factory to
 * an object that publishes transactional metrics without the object needing to know or care whether or not its metrics are
 * enabled or not.
 */
public class NoOpTransaction extends AbstractTransaction implements Transaction, ManualTransaction {

    private Long startMillis;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Constructs no-op transaction.
     */
    protected NoOpTransaction() {
        super(null, null, null, null);
    }

    @Override
    public Transaction start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            startMillis = currMillis();

            return this;

        } else {
            throw new IllegalStateException("Transaction already started.");
        }
    }

    @Override
    public Duration success() {
        return stop(Status.success());
    }

    @Override
    public Duration success(final Duration duration) {
        return duration;
    }

    @Override
    public Duration error() {
        return stop(Status.error());
    }

    @Override
    public Duration error(final Duration duration) {
        return duration;
    }

    @Override
    public Duration warn() {
        return stop(Status.warning());
    }

    @Override
    public Duration warn(final Integer level) {
        return stop(Status.warning(level));
    }

    @Override
    public Duration warn(final Duration duration) {
        return duration;
    }

    @Override
    public Duration warn(final Integer level, final Duration duration) {
        return duration;
    }

    @Override
    public Duration stop(final Status callStatus) {
        assertRunning(Boolean.TRUE);
        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        isRunning.set(Boolean.FALSE);
        return elapsed;
    }

    @Override
    public Duration stop(final Status callStatus, final Duration duration) {
        return duration;
    }

    @Override
    public Boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public Long elapsedMillis() {
        assertRunning(Boolean.TRUE);
        return currMillis() - startMillis;
    }

    @Override
    public void close() throws Exception {
        stop(Status.success());
    }

    private Long currMillis() {
        return System.currentTimeMillis();
    }

    private void assertRunning(final boolean expected) {
        if (this.isRunning.get() != expected) {
            throw new IllegalStateException("Transaction not in expected state.");
        }
    }

}
