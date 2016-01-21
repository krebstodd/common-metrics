package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;
import com.codahale.metrics.Counter;

public class BpCounter extends BpMetric<Long> {

    private final Counter counter;

    public BpCounter(final String name, final String description) {
        this(new Counter(), name, description);
    }

    public BpCounter(final Counter counter, final String name, final String description) {
        super(name, description);
        this.counter = counter;
    }

    public void increment() {
        increment(1L);
    }

    public void increment(final Long incrementBy) {
        recordEvent(eventSample(incrementBy));
        counter.inc(incrementBy);
    }

    public void decrement() {
        decrement(1L);
    }

    public void decrement(final Long decrementBy) {
        recordEvent(eventSample(decrementBy * -1));
        counter.dec(decrementBy);
    }

    public Long getCount() {
        return this.counter.getCount();
    }

    // CHECK_OFF: MagicNumber
    private EventSample<Long> eventSample(final Long by) {
        final ImmutablePair[] sample = new ImmutablePair[2];

        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("amount", by);

        return new EventSample<>(getName(), sample, SampleType.EVENT, by);
    }

    @Override
    public Sample aggregateSample() {
        final ImmutablePair[] sample = new ImmutablePair[3];
        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("count", getCount());
        return new Sample(getName(), sample, SampleType.AGGREGATE);
    }
    // CHECK_ON: MagicNumber

}
