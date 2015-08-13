package com.blispay.common.metrics;

import com.codahale.metrics.Meter;

public class BpMeter extends BpMetric {

    private final Meter meter;

    public BpMeter(final String name, final String description) {
        super(name, description);
        this.meter = new Meter();
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

    @Override
    public ImmutablePair[] sample() {
        final ImmutablePair[] sample = new ImmutablePair[5];

        sample[0] = new ImmutablePair("count", this.meter.getCount());
        sample[1] = new ImmutablePair("meanRate", this.meter.getMeanRate());
        sample[2] = new ImmutablePair("oneMinuteRate", this.meter.getOneMinuteRate());
        sample[3] = new ImmutablePair("fiveMinuteRate", this.meter.getFiveMinuteRate());
        sample[4] = new ImmutablePair("fifteenMinuteRate", this.meter.getFifteenMinuteRate());

        return sample;
    }

    @Override
    Meter getInternalMetric() {
        return this.meter;
    }

}
