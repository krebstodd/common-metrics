package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.util.LocalMetricContext;

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

    private ZonedDateTime timestamp;
    private Direction direction;
    private Action action;
    private Resource resource;

    private String message;
    private Map<String, Object> notes = new HashMap<>();

    public Transaction(final EventRepository<TransactionData> repository) {
        this.repository = repository;
    }

    public Transaction inGroup(final EventGroup group) {
        this.repository.inGroup(group);
        return this;
    }

    public Transaction withName(final String name) {
        this.repository.withName(name);
        return this;
    }

    public Transaction withNameFromType(final Class<?> type) {
        this.repository.withNameFromType(type);
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

    public Transaction makeNote(final String noteName, final Object note) {
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

    public Transaction start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            timestamp = ZonedDateTime.now();

            startMillis = currMillis();

            return this;

        } else {
            throw new IllegalStateException("Stopwatch already started.");
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

    public Duration stop(final Status callStatus) {
        assertRunning(Boolean.TRUE);
        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        isRunning.set(Boolean.FALSE);

        repository.withTimestamp(timestamp)
                .save(build(elapsed, callStatus));

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