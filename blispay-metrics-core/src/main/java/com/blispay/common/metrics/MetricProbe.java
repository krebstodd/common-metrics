package com.blispay.common.metrics;

import com.blispay.common.metrics.util.StartupPhase;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MetricProbe implements SmartLifecycle {

    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public void start() {
        isRunning.set(true);
    }

    @Override
    public void stop() {
        isRunning.set(false);
    }

    @Override
    public void stop(final Runnable runnable) {
        runnable.run();
        isRunning.set(false);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public int getPhase() {
        return StartupPhase.PROBE.value();
    }
}
