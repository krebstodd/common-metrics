package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.util.StartupPhase;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class EventDispatcher implements SmartLifecycle {

    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    public abstract void dispatch(final EventModel evt);

    public abstract EventEmitter newEventEmitter();

    public abstract void subscribe(final EventSubscriber listener);

    public abstract void unSubscribe(final EventSubscriber listener);

    protected AtomicBoolean isRunningAtomic() {
        return isRunning;
    }

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public int getPhase() {
        return StartupPhase.DISPATCHER.value();
    }

}
