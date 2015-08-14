package com.blispay.common.metrics;

import com.codahale.metrics.Meter;

public class BpMeter extends BpMetric {

    private final Meter meter;

    public BpMeter(final String name, final String description) {
        super(name, description);
        this.meter = new Meter();
    }

    public BpMeter(final Meter meter, final String name, final String description) {
        super(name, description);
        this.meter = meter;
    }

    public void mark() {
        meter.mark();
    }

    public void mark(final Long numberOfOccurences) {
        meter.mark(numberOfOccurences);
    }

    public void mark(final Integer numberOfOccurrences) {
        mark(Long.valueOf(numberOfOccurrences));
    }

    public long getCount() {
        return meter.getCount();
    }

    public double getMeanRate() {
        return this.meter.getMeanRate();
    }

    public double getOneMinuteRate() {
        return this.meter.getMeanRate();
    }

    public double getFiveMinuteRate() {
        return this.meter.getMeanRate();
    }

    public double getFifteenMinuteRate() {
        return this.meter.getMeanRate();
    }

    // CHECK_OFF: MagicNumber
    @Override
    public Sample sample() {
        final ImmutablePair[] sample = new ImmutablePair[7];

        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("count", getCount());
        sample[3] = new ImmutablePair("meanRate", getMeanRate());
        sample[4] = new ImmutablePair("oneMinuteRate", getOneMinuteRate());
        sample[5] = new ImmutablePair("fiveMinuteRate", getFiveMinuteRate());
        sample[6] = new ImmutablePair("fifteenMinuteRate", getFifteenMinuteRate());

        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

}
