package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.util.StartupPhase;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

// TODO - Investigate any lightweight frameworks that do in-memory dispatching we can wrap in this interface. May be over-engineering.
public abstract class EventDispatcher implements SmartLifecycle {

    protected final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    public abstract void dispatch(final BaseMetricModel evt);

    public abstract EventEmitter newEventEmitter();

    public abstract void subscribe(final EventSubscriber listener);

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
