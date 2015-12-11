package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;

public class BpHistogram extends BpMetric {

    private final Histogram histogram;

    public BpHistogram(final String name, final String description) {
        super(name, description);
        this.histogram = new Histogram(new ExponentiallyDecayingReservoir());
    }

    public BpHistogram(final Histogram histogram, final String name, final String description) {
        super(name, description);
        this.histogram = histogram;
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

    public Double getMedian() {
        return histogram.getSnapshot().getMedian();
    }

    public Double getMean() {
        return histogram.getSnapshot().getMean();
    }

    public Double get75thPercentile() {
        return histogram.getSnapshot().get75thPercentile();
    }

    public Double get95thPercentile() {
        return histogram.getSnapshot().get95thPercentile();
    }

    public Double get98thPercentile() {
        return histogram.getSnapshot().get98thPercentile();
    }

    public Double get99thPercentile() {
        return histogram.getSnapshot().get99thPercentile();
    }

    public Double get999thPercentile() {
        return histogram.getSnapshot().get999thPercentile();
    }

    public long getMax() {
        return histogram.getSnapshot().getMax();
    }

    public long getMin() {
        return histogram.getSnapshot().getMin();
    }

    public long[] getValues() {
        return histogram.getSnapshot().getValues();
    }

    // CHECK_OFF: MagicNumber
    @Override
    public Sample sample() {
        final ImmutablePair[] sample = new ImmutablePair[12];

        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("count", getCount());
        sample[3] = new ImmutablePair("median", getMedian());
        sample[4] = new ImmutablePair("mean", getMean());
        sample[5] = new ImmutablePair("75thPercentile", get75thPercentile());
        sample[6] = new ImmutablePair("95thPercentile", get95thPercentile());
        sample[7] = new ImmutablePair("98thPercentile", get98thPercentile());
        sample[8] = new ImmutablePair("99thPercentile", get99thPercentile());
        sample[9] = new ImmutablePair("999thPercentile", get999thPercentile());
        sample[10] = new ImmutablePair("max", getMax());
        sample[11] = new ImmutablePair("min", getMin());

        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

}
