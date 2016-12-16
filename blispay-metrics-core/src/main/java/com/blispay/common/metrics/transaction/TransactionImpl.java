package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NameFormatter;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class TransactionImpl.
 */
public class TransactionImpl implements Transaction {

    private Long startMillis;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private Direction direction;
    private Action action;
    private Resource resource;

    private ZonedDateTime timestamp;
    private final EventEmitter emitter;
    private final String appId;
    private final EventGroup group;

    private String name;
    private Object userData;

    /**
     * Constructs TransactionImpl.
     *
     * @param emitter emitter.
     * @param appId appId.
     * @param group group.
     * @param name name.
     */
    protected TransactionImpl(final EventEmitter emitter, final String appId, final EventGroup group, final String name) {

        this.emitter = emitter;
        this.appId = appId;
        this.group = group;
        this.name = name;
    }

    /**
     * Method withName.
     *
     * @param name name.
     * @return return value.
     */
    public Transaction withName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Method withNameFromType.
     *
     * @param type type.
     * @return return value.
     */
    public Transaction withNameFromType(final Class<?> type) {
        this.name = NameFormatter.toEventName(type);
        return this;
    }

    /**
     * Method inDirection.
     *
     * @param direction direction.
     * @return return value.
     */
    public Transaction inDirection(final Direction direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Method withAction.
     *
     * @param action action.
     * @return return value.
     */
    public Transaction withAction(final Action action) {
        this.action = action;
        return this;
    }

    /**
     * Method onResource.
     *
     * @param resource resource.
     * @return return value.
     */
    public Transaction onResource(final Resource resource) {
        this.resource = resource;
        return this;
    }

    /**
     * Method userData.
     *
     * @param userData userData.
     * @return return value.
     */
    public Transaction userData(final Object userData) {
        this.userData = userData;
        return this;
    }

    private TransactionData build(final Duration duration, final Status status) {
        return new TransactionData<>(direction, duration.toMillis(), resource, action, status);
    }

    /**
     * Start the current transaction.
     * @return The currently running tx.
     */
    public Transaction start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            timestamp = ZonedDateTime.now(ZoneId.of("UTC"));

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

    /**
     * Stop the currently running transaction with a custom status code.
     * @param callStatus The status of the completed transaction.
     * @return The total duration of the transaction.
     */
    public Duration stop(final Status callStatus) {
        assertRunning(Boolean.TRUE);
        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        isRunning.set(Boolean.FALSE);

        emitter.emit(createModel(build(elapsed, callStatus), userData));

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

    private Long currMillis() {
        return System.currentTimeMillis();
    }

    private void assertRunning(final boolean expected) {
        if (this.isRunning.get() != expected) {
            throw new IllegalStateException("Transaction not in expected state.");
        }
    }

    private <U> EventModel<TransactionData, U> createModel(final TransactionData data, final U userData) {
        return new EventModel<>(createHeader(), data, userData);
    }

    private EventHeader createHeader() {
        return EventHeader.builder().timestamp(timestamp).applicationId(appId).group(group).type(EventType.TRANSACTION).trackingInfo(LocalMetricContext.getTrackingInfo()).name(name).build();
    }

    @Override
    public void close() {
        stop(Status.success());
    }

}
