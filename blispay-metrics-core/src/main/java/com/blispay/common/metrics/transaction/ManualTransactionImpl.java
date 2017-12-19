package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;

/**
 * Implementation of the {@link ManualTransaction} interface.
 */
public class ManualTransactionImpl extends AbstractTransaction implements ManualTransaction {

    /**
     * Constructs new manual transaction.
     *
     * @param emitter emitter.
     * @param appId   appId.
     * @param group   group.
     * @param name    name.
     */
    protected ManualTransactionImpl(final EventEmitter emitter, final String appId, final EventGroup group, final String name) {
        super(emitter, appId, group, name);
    }

    @Override
    public Duration success(final Duration duration) {
        return stop(Status.success(), duration);
    }

    @Override
    public Duration error(final Duration duration) {
        return stop(Status.error(), duration);
    }

    @Override
    public Duration warn(final Duration duration) {
        return stop(Status.warning(), duration);
    }

    @Override
    public Duration warn(final Integer level, final Duration duration) {
        return stop(Status.warning(level), duration);
    }

    @Override
    public Duration stop(final Status callStatus, final Duration duration) {
        setTimestamp();
        emit(duration, callStatus);
        return duration;
    }

}
