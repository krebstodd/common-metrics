package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class TransactionImpl.
 */
public class TransactionImpl extends AbstractTransaction implements Transaction {

    private Long startMillis;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Constructs AbstractTransaction.
     *
     * @param emitter emitter.
     * @param appId   appId.
     * @param group   group.
     * @param name    name.
     */
    protected TransactionImpl(final EventEmitter emitter, final String appId, final EventGroup group, final String name) {
        super(emitter, appId, group, name);
    }

    @Override
    public Transaction withName(final String name) {
        setName(name);
        return this;
    }

    @Override
    public Transaction withNameFromType(final Class type) {
        setNameFromType(type);
        return this;
    }

    @Override
    public Transaction inDirection(final Direction direction) {
        setDirection(direction);
        return this;
    }

    @Override
    public Transaction withAction(final Action action) {
        setAction(action);
        return this;
    }

    @Override
    public Transaction onResource(final Resource resource) {
        setResource(resource);
        return this;
    }

    @Override
    public Transaction userData(final Object userData) {
        setUserData(userData);
        return this;
    }

    /**
     * Start the current transaction.
     * @return The currently running tx.
     */
    @Override
    public TransactionImpl start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            setTimestamp();

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
    @Override
    public Duration success() {
        return stop(Status.success());
    }

    /**
     * Method error.
     *
     * @return return value.
     */
    @Override
    public Duration error() {
        return stop(Status.error());
    }

    /**
     * Method warn.
     *
     * @return return value.
     */
    @Override
    public Duration warn() {
        return stop(Status.warning());
    }

    /**
     * Method warn.
     *
     * @param level level.
     * @return return value.
     */
    @Override
    public Duration warn(final Integer level) {
        return stop(Status.warning(level));
    }

    /**
     * Stop the currently running transaction with a custom status code.
     * @param callStatus The status of the completed transaction.
     * @return The total duration of the transaction.
     */
    @Override
    public Duration stop(final Status callStatus) {
        assertRunning(Boolean.TRUE);
        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        isRunning.set(Boolean.FALSE);
        emit(elapsed, callStatus);
        return elapsed;
    }

    /**
     * Method isRunning.
     *
     * @return return value.
     */
    @Override
    public Boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Method elapsedMillis.
     *
     * @return return value.
     */
    @Override
    public Long elapsedMillis() {
        assertRunning(Boolean.TRUE);
        return currMillis() - startMillis;
    }

    private Long currMillis() {
        return System.currentTimeMillis();
    }

    private void assertRunning(final boolean expected) {
        if (this.isRunning.get() != expected) {
            throw new IllegalStateException("Transaction not in expected state.");
        }
    }

    @Override
    public void close() {
        stop(Status.success());
    }

}
