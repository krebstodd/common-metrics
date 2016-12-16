package com.blispay.common.metrics.event;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.util.StartupPhase;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class EventDispatcher.
 */
public abstract class EventDispatcher implements SmartLifecycle {

    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    /**
     * Method dispatch.
     *
     * @param evt evt.
     */
    public abstract void dispatch(EventModel evt);

    /**
     * Method newEventEmitter.
     *
     * @return return value.
     */
    public abstract EventEmitter newEventEmitter();

    /**
     * Method subscribe.
     *
     * @param listener listener.
     */
    public abstract void subscribe(EventSubscriber listener);

    /**
     * Method unSubscribe.
     *
     * @param listener listener.
     */
    public abstract void unSubscribe(EventSubscriber listener);

    /**
     * Method isRunningAtomic.
     *
     * @return return value.
     */
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
