package com.blispay.common.metrics;

import com.blispay.common.metrics.util.StartupPhase;
import org.springframework.context.SmartLifecycle;

public abstract class MetricProbe implements SmartLifecycle {

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(final Runnable runnable) {
        runnable.run();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return StartupPhase.PROBE.value();
    }
}
