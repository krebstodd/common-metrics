package com.blispay.common.metrics.metric;

import org.springframework.context.Lifecycle;

public interface MetricProbe extends Lifecycle {

    @Override
    default void start() {

    }

    @Override
    default void stop() {

    }

    @Override
    default boolean isRunning() {
        return true;
    }

}
