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

/**
 * Base transaction implementation. Includes management of top level {@link TransactionMetadata} and emitting new
 * transaction metrics.
 */
public abstract class AbstractTransaction implements TransactionMetadata {

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
     * Constructs AbstractTransaction.
     *
     * @param emitter emitter.
     * @param appId appId.
     * @param group group.
     * @param name name.
     */
    protected AbstractTransaction(final EventEmitter emitter, final String appId, final EventGroup group, final String name) {
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
    @Override
    public <T extends AbstractTransaction> T withName(final String name) {
        this.name = name;
        return (T) this;
    }

    /**
     * Method withNameFromType.
     *
     * @param type type.
     * @return return value.
     */
    @Override
    public <T extends AbstractTransaction> T withNameFromType(final Class<?> type) {
        this.name = NameFormatter.toEventName(type);
        return (T) this;
    }

    /**
     * Method inDirection.
     *
     * @param direction direction.
     * @return return value.
     */
    @Override
    public <T extends AbstractTransaction> T inDirection(final Direction direction) {
        this.direction = direction;
        return (T) this;
    }

    /**
     * Method withAction.
     *
     * @param action action.
     * @return return value.
     */
    @Override
    public <T extends AbstractTransaction> T withAction(final Action action) {
        this.action = action;
        return (T) this;
    }

    /**
     * Method onResource.
     *
     * @param resource resource.
     * @return return value.
     */
    @Override
    public <T extends AbstractTransaction> T onResource(final Resource resource) {
        this.resource = resource;
        return (T) this;
    }

    /**
     * Method userData.
     *
     * @param userData userData.
     * @return return value.
     */
    @Override
    public <T extends AbstractTransaction> T userData(final Object userData) {
        this.userData = userData;
        return (T) this;
    }

    protected void emit(final Duration elapsed, final Status callStatus) {
        emitter.emit(createModel(build(elapsed, callStatus), userData));
    }

    protected void setTimestamp() {
        this.timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    protected <U> EventModel<TransactionData, U> createModel(final TransactionData data, final U userData) {
        return new EventModel<>(createHeader(), data, userData);
    }

    private TransactionData build(final Duration duration, final Status status) {
        return new TransactionData<>(direction, duration.toMillis(), resource, action, status);
    }

    private EventHeader createHeader() {
        return EventHeader.builder()
                .timestamp(timestamp)
                .applicationId(appId)
                .group(group)
                .type(EventType.TRANSACTION)
                .trackingInfo(LocalMetricContext.getTrackingInfo())
                .name(name)
                .build();
    }

}
