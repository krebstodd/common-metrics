package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class NoOpTransaction.
 */
public class NoOpTransaction implements Transaction {

    private Long startMillis;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public Transaction withName(final String name) {
        return this;
    }

    @Override
    public Transaction withNameFromType(final Class<?> type) {
        return this;
    }

    @Override
    public Transaction inDirection(final Direction direction) {
        return this;
    }

    @Override
    public Transaction withAction(final Action action) {
        return this;
    }

    @Override
    public Transaction onResource(final Resource resource) {
        return this;
    }

    @Override
    public Transaction userData(final Object userData) {
        return this;
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

    /**
     * Method success.
     *
     * @return return value.
     */
    public Duration success() {
        return stop(Status.success());
    }

    /**
     * Method error.
     *
     * @return return value.
     */
    public Duration error() {
        return stop(Status.error());
    }

    /**
     * Method warn.
     *
     * @return return value.
     */
    public Duration warn() {
        return stop(Status.warning());
    }

    /**
     * Method warn.
     *
     * @param level level.
     * @return return value.
     */
    public Duration warn(final Integer level) {
        return stop(Status.warning(level));
    }

    private Long currMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Stop the currently running transaction with a custom status code.
     * @param callStatus The status of the completed transaction.
     * @return The total duration of the transaction.
     */
    public Duration stop(final Status callStatus) {
        assertRunning(Boolean.TRUE);
        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        isRunning.set(Boolean.FALSE);
        return elapsed;
    }

    /**
     * Method isRunning.
     *
     * @return return value.
     */
    public Boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Method elapsedMillis.
     *
     * @return return value.
     */
    public Long elapsedMillis() {
        assertRunning(Boolean.TRUE);
        return currMillis() - startMillis;
    }

    private void assertRunning(final boolean expected) {
        if (this.isRunning.get() != expected) {
            throw new IllegalStateException("Transaction not in expected state.");
        }
    }

    @Override
    public void close() throws Exception {
        stop(Status.success());
    }

}
