package com.blispay.common.metrics;

import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NotYetStartedException;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transaction implements AutoCloseable {

    private Long startMillis;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private final EventRepository<TransactionData> repository;

    private final TrackingInfo trackingInfo = LocalMetricContext.getTrackingInfo();

    private String name;
    private ZonedDateTime timestamp;
    private Direction direction;
    private Action action;
    private Resource resource;

    private String message;
    private Map<String, Object> notes = new HashMap<>();

    public Transaction(final EventRepository<TransactionData> repository) {
        this.repository = repository;
    }

    public Transaction withName(final String name) {
        this.name = name;
        return this;
    }

    public Transaction inDirection(final Direction direction) {
        this.direction = direction;
        return this;
    }

    public Transaction withAction(final Action action) {
        this.action = action;
        return this;
    }

    public Transaction onResource(final Resource resource) {
        this.resource = resource;
        return this;
    }

    public Transaction withNote(final String noteName, final Object note) {
        this.notes.put(noteName, note);
        return this;
    }

    private TransactionData build(final Duration duration, final Status status) {
        return new TransactionData<>(direction, duration.toMillis(), resource, action, status, message, trackingInfo);
    }

    public Transaction message(final String message) {
        this.message = message;
        return this;
    }

    /**
     * Start the current transaction.
     * @return The currently running tx.
     */
    public Transaction start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            timestamp = ZonedDateTime.now();

            startMillis = currMillis();

            return this;

        } else {
            throw new IllegalStateException("Transaction already started.");
        }
    }

    public Duration success() {
        return stop(Status.success());
    }

    public Duration error() {
        return stop(Status.error());
    }

    public Duration warn() {
        return stop(Status.warning());
    }

    public Duration warn(final Integer level) {
        return stop(Status.warning(level));
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

        try {

            repository.save(this.name, timestamp, build(elapsed, callStatus));

        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {
            LoggerFactory.getLogger(Transaction.class).error("Caught exception saving transaction...");

            if (ex instanceof NotYetStartedException) {
                throw ex;
            }
        }
        // CHECK_ON: IllegalCatch

        return elapsed;
    }

    public Boolean isRunning() {
        return isRunning.get();
    }

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