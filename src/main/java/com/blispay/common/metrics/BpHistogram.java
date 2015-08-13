package com.blispay.common.metrics;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Snapshot;

public class BpHistogram extends BpMetric {

    private final Histogram histogram;

    public BpHistogram(final String name, final String description) {
        super(name, description);
        this.histogram = new Histogram(new ExponentiallyDecayingReservoir());
    }

    public void update(final Integer value) {
        histogram.update(value);
    }

    public void update(final Long value) {
        histogram.update(value);
    }

    public Long getCount() {
        return histogram.getCount();
    }

    @Override
    public ImmutablePair[] sample() {
        final ImmutablePair[] sample = new ImmutablePair[11];
        final Snapshot sn = this.histogram.getSnapshot();
        final Long count = this.histogram.getCount();

        sample[0] = new ImmutablePair("count", count);
        sample[1] = new ImmutablePair("median", sn.getMedian());
        sample[2] = new ImmutablePair("mean", sn.getMean());
        sample[3] = new ImmutablePair("75thPercentile", sn.get75thPercentile());
        sample[4] = new ImmutablePair("95thPercentile", sn.get95thPercentile());
        sample[5] = new ImmutablePair("98thPercentile", sn.get98thPercentile());
        sample[6] = new ImmutablePair("99thPercentile", sn.get99thPercentile());
        sample[7] = new ImmutablePair("999thPercentile", sn.get999thPercentile());
        sample[8] = new ImmutablePair("max", sn.getMax());
        sample[9] = new ImmutablePair("min", sn.getMin());
        sample[10] = new ImmutablePair("mean", sn.getMean());

        return sample;
    }

    @Override
    Histogram getInternalMetric() {
        return this.histogram;
    }
}
