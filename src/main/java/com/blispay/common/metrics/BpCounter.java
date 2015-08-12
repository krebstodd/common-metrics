package com.blispay.common.metrics;

import com.codahale.metrics.Counter;

public class BpCounter extends BpMetric {

    private final Counter counter;

    public BpCounter(final String name, final String description) {
        super(name, description);
        this.counter = new Counter();
    }

    public void increment() {
        counter.inc();
    }

    public void decrement() {
        counter.dec();
    }

    public void increment(final Long incrementBy) {
        counter.inc(incrementBy);
    }

    public void decrement(final Long decrementBy) {
        counter.dec(decrementBy);
    }

    @Override
    ImmutablePair[] sample() {
        final ImmutablePair[] sample = new ImmutablePair[1];
        sample[0] = new ImmutablePair("count", this.counter.getCount());
        return sample;
    }

    @Override
    Counter getInternalMetric() {
        return this.counter;
    }

}
