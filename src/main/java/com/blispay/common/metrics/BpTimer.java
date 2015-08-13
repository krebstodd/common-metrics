package com.blispay.common.metrics;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BpTimer extends BpMetric {

    private final Timer timer;

    public BpTimer(final String name, final String description) {
        super(name, description);
        this.timer = new Timer();
    }

    public <T> T time(Callable<T> event) throws Exception {
        return timer.time(event);
    }

    public Resolver time() {
        return timer.time()::stop;
    }

    @FunctionalInterface
    public static interface Resolver {
        void done();
    }

    @Override
    public ImmutablePair[] sample() {
        final ImmutablePair[] sample = new ImmutablePair[15];

        sample[0] = new ImmutablePair("count", this.timer.getCount());
        sample[1] = new ImmutablePair("meanRate", this.timer.getMeanRate());
        sample[2] = new ImmutablePair("oneMinuteRate", this.timer.getOneMinuteRate());
        sample[3] = new ImmutablePair("fiveMinuteRate", this.timer.getFiveMinuteRate());
        sample[4] = new ImmutablePair("fifteenMinuteRate", this.timer.getFifteenMinuteRate());

        final Snapshot sn = this.timer.getSnapshot();

        sample[5] = new ImmutablePair("median", sn.getMedian());
        sample[6] = new ImmutablePair("mean", sn.getMean());
        sample[7] = new ImmutablePair("75thPercentile", sn.get75thPercentile());
        sample[8] = new ImmutablePair("95thPercentile", sn.get95thPercentile());
        sample[9] = new ImmutablePair("98thPercentile", sn.get98thPercentile());
        sample[10] = new ImmutablePair("99thPercentile", sn.get99thPercentile());
        sample[11] = new ImmutablePair("999thPercentile", sn.get999thPercentile());
        sample[12] = new ImmutablePair("max", sn.getMax());
        sample[13] = new ImmutablePair("min", sn.getMin());
        sample[14] = new ImmutablePair("mean", sn.getMean());

        return sample;
    }

    @Override
    Timer getInternalMetric() {
        return this.timer;
    }
}
