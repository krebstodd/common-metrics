package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;
import com.codahale.metrics.Counter;

public class BpCounter extends BpMetric<Long> {

    /**
     * Default event key name.
     */
    public static final String DEFAULT_EVENT_KEY = "increment";

    private final Counter counter;

    public BpCounter(final Class<?> owner, final String name, final String description) {
        this(new Counter(), owner, name, description);
    }

    public BpCounter(final Counter counter, final Class<?> owner, final String name, final String description) {
        super(owner, name, description);
        this.counter = counter;
    }

    public void increment() {
        increment(1L);
    }

    public void increment(final Long incrementBy) {
        publishEvent(DEFAULT_EVENT_KEY, incrementBy);
        counter.inc(incrementBy);
    }

    public void decrement() {
        decrement(1L);
    }

    public void decrement(final Long decrementBy) {
        counter.dec(decrementBy);
        publishEvent(DEFAULT_EVENT_KEY, negate(decrementBy));
    }

    public Long getCount() {
        return this.counter.getCount();
    }

    private Long negate(final Long absolute) {
        return absolute * -1L;
    }

    // CHECK_OFF: MagicNumber

    @Override
    public Sample aggregateSample() {
        final ImmutablePair[] sample = new ImmutablePair[3];
        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("count", getCount());
        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

}
