package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;
import com.codahale.metrics.Meter;

public class BpMeter extends BpMetric<Long> {

    private final Meter meter;

    public BpMeter(final String name, final String description) {
        this(new Meter(), name, description);
    }

    public BpMeter(final Meter meter, final String name, final String description) {
        super(name, description);
        this.meter = meter;
    }

    public void mark() {
        mark(1L);
    }

    public void mark(final Integer numberOfOccurrences) {
        mark(Long.valueOf(numberOfOccurrences));
    }

    public void mark(final Long numberOfOccurrences) {
        recordEvent(eventSample(numberOfOccurrences));
        meter.mark(numberOfOccurrences);
    }

    public Long getCount() {
        return meter.getCount();
    }

    public Double getMeanRate() {
        return this.meter.getMeanRate();
    }

    public Double getOneMinuteRate() {
        return this.meter.getMeanRate();
    }

    public Double getFiveMinuteRate() {
        return this.meter.getMeanRate();
    }

    public Double getFifteenMinuteRate() {
        return this.meter.getMeanRate();
    }

    // CHECK_OFF: MagicNumber

    private EventSample<Long> eventSample(final Long newOccurrences) {
        final ImmutablePair[] sample = new ImmutablePair[2];

        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("numOccurrences", newOccurrences);

        return new EventSample<>(getName(), sample, SampleType.EVENT, newOccurrences);
    }

    @Override
    public Sample aggregateSample() {
        final ImmutablePair[] sample = new ImmutablePair[8];

        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("rateUnit", getRateUnit());
        sample[3] = new ImmutablePair("count", getCount());
        sample[4] = new ImmutablePair("meanRate", getMeanRate());
        sample[5] = new ImmutablePair("oneMinuteRate", getOneMinuteRate());
        sample[6] = new ImmutablePair("fiveMinuteRate", getFiveMinuteRate());
        sample[7] = new ImmutablePair("fifteenMinuteRate", getFifteenMinuteRate());

        return new Sample(getName(), sample, SampleType.AGGREGATE);
    }
    // CHECK_ON: MagicNumber

}
