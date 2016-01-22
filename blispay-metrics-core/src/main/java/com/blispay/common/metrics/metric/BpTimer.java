package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;
import com.blispay.common.metrics.util.StopWatch;
import com.codahale.metrics.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BpTimer extends BpMetric<Long> {

    private static Boolean DEFAULT_PUBLISH_EVENTS = Boolean.TRUE;

    private final Timer timer;

    public BpTimer(final Class<?> owner, final String name, final String description) {
        this(new Timer(), owner, name, description);
    }

    public BpTimer(final Timer timer, final Class<?> owner, final String name, final String description) {
        super(owner, name, description, DEFAULT_PUBLISH_EVENTS);
        this.timer = timer;
    }

    public void update(final long duration, final TimeUnit timeUnit) {
        timer.update(duration, timeUnit);
    }

    public <T> T time(final Callable<T> event) throws Exception {
        return timer.time(event);
    }

    /**
     * Start a new stopwatch.
     *
     * @return A new, started stopwatch instance.
     */
    public StopWatch time() {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.setCompletionNotifier(elapsedMillis -> this.timer.update(elapsedMillis, TimeUnit.MILLISECONDS));
        stopWatch.setLapNotifier(this::publishEvent);

        stopWatch.start();

        return stopWatch;
    }

    public long getCount() {
        return timer.getCount();
    }

    public Double getMeanRate() {
        return timer.getMeanRate();
    }

    public Double getOneMinuteRate() {
        return timer.getOneMinuteRate();
    }

    public Double getFiveMinuteRate() {
        return timer.getFiveMinuteRate();
    }

    public Double getFifteenMinuteRate() {
        return timer.getFifteenMinuteRate();
    }

    public Double getMedian() {
        return timer.getSnapshot().getMedian();
    }

    public Double getMean() {
        return timer.getSnapshot().getMean();
    }

    public Double get75thPercentile() {
        return timer.getSnapshot().get75thPercentile();
    }

    public Double get95thPercentile() {
        return timer.getSnapshot().get95thPercentile();
    }

    public Double get98thPercentile() {
        return timer.getSnapshot().get98thPercentile();
    }

    public Double get99thPercentile() {
        return timer.getSnapshot().get99thPercentile();
    }

    public Double get999thPercentile() {
        return timer.getSnapshot().get999thPercentile();
    }

    public Long getMax() {
        return timer.getSnapshot().getMax();
    }

    public Long getMin() {
        return timer.getSnapshot().getMin();
    }

    public long[] getValues() {
        return timer.getSnapshot().getValues();
    }

    // CHECK_OFF: MagicNumber
    @Override
    public Sample aggregateSample() {
        final ImmutablePair[] sample = new ImmutablePair[19];

        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("durationUnit", getDurationUnit());
        sample[3] = new ImmutablePair("rateUnit", getRateUnit());
        sample[4] = new ImmutablePair("count", getCount());
        sample[5] = new ImmutablePair("meanRate", getMeanRate());
        sample[6] = new ImmutablePair("oneMinuteRate", getOneMinuteRate());
        sample[7] = new ImmutablePair("fiveMinuteRate", getFiveMinuteRate());
        sample[8] = new ImmutablePair("fifteenMinuteRate", getFifteenMinuteRate());
        sample[9] = new ImmutablePair("median", getMedian());
        sample[10] = new ImmutablePair("mean", getMean());
        sample[11] = new ImmutablePair("75thPercentile", get75thPercentile());
        sample[12] = new ImmutablePair("95thPercentile", get95thPercentile());
        sample[13] = new ImmutablePair("98thPercentile", get98thPercentile());
        sample[14] = new ImmutablePair("99thPercentile", get99thPercentile());
        sample[15] = new ImmutablePair("999thPercentile", get999thPercentile());
        sample[16] = new ImmutablePair("max", getMax());
        sample[17] = new ImmutablePair("min", getMin());
        sample[18] = new ImmutablePair("mean", getMean());

        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

    @FunctionalInterface
    public interface Resolver {
        void done();
    }

}
