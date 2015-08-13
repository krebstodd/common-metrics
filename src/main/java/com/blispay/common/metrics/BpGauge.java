package com.blispay.common.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;

import java.util.function.Supplier;

public class BpGauge<T> extends BpMetric {

    private final Gauge<T> gauge;

    public BpGauge(final String name, final String description, Supplier<T> supplier) {
        super(name, description);
        this.gauge = supplier::get;
    }

    @Override
    public ImmutablePair[] sample() {
        final ImmutablePair[] sample = new ImmutablePair[1];
        sample[0] = new ImmutablePair("currentValue", gauge.getValue());
        return sample;
    }

    @Override
    Gauge<T> getInternalMetric() {
        return gauge;
    }
}
