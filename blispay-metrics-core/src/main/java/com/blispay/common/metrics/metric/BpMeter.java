package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;
import com.blispay.common.metrics.util.MetricEventKey;
import com.codahale.metrics.Meter;

public class BpMeter extends BpMetric<Long> {

    /**
     * Default event key name.
     */
    public static final String DEFAULT_EVENT_KEY = "newOccurrences";

    private final Meter meter;

    public BpMeter(final Class<?> owner, final String name, final String description) {
        this(new Meter(), owner, name, description);
    }

    public BpMeter(final Meter meter, final Class<?> owner, final String name, final String description) {
        super(owner, name, description);
        this.meter = meter;
    }

    public void mark() {
        mark(1L);
    }

    public void mark(final Integer numberOfOccurrences) {
        mark(Long.valueOf(numberOfOccurrences));
    }

    public void mark(final Long numberOfOccurrences) {
        mark(DEFAULT_EVENT_KEY, numberOfOccurrences);
    }

    public void mark(final MetricEventKey eventKey) {
        mark(eventKey, 1L);
    }

    public void mark(final MetricEventKey eventKey, final Long numberOfOccurrences) {
        mark(eventKey.buildKey(), numberOfOccurrences);
    }

    public void mark(final String eventKey, final Long numberOfOccurrences) {
        publishEvent(eventKey, numberOfOccurrences);
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

        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

}
