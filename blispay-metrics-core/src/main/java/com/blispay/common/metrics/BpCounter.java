package com.blispay.common.metrics;

import com.codahale.metrics.Counter;

public class BpCounter extends BpMetric {

    private final Counter counter;

    public BpCounter(final String name, final String description) {
        super(name, description);
        this.counter = new Counter();
    }

    public BpCounter(final Counter counter, final String name, final String description) {
        super(name, description);
        this.counter = counter;
    }

    public void increment() {
        counter.inc();
    }

    public void increment(final Long incrementBy) {
        counter.inc(incrementBy);
    }

    public void decrement() {
        counter.dec();
    }

    public void decrement(final Long decrementBy) {
        counter.dec(decrementBy);
    }

    public long getCount() {
        return this.counter.getCount();
    }

    // CHECK_OFF: MagicNumber
    @Override
    public Sample sample() {
        final ImmutablePair[] sample = new ImmutablePair[3];
        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("count", getCount());
        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

}
