package com.blispay.common.metrics;

import com.codahale.metrics.Gauge;

import java.util.function.Supplier;

public class BpGauge<T> extends BpMetric {

    private final Gauge<T> gauge;

    public BpGauge(final String name, final String description, final Supplier<T> supplier) {
        super(name, description);
        this.gauge = supplier::get;
    }

    public BpGauge(final Gauge<T> gauge, final String name, final String description) {
        super(name, description);
        this.gauge = gauge;
    }

    public T getValue() {
        return this.gauge.getValue();
    }

    // CHECK_OFF: MagicNumber
    @Override
    public Sample sample() {
        final ImmutablePair[] sample = new ImmutablePair[3];
        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("currentValue", getValue());
        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

}
