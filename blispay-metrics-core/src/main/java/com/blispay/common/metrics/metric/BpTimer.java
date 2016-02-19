package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.util.StopWatch;
import com.codahale.metrics.Timer;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BpTimer extends BpMetric {

    private static final MetricType mType = MetricType.PERFORMANCE;

    private final Timer timer;

    private Function<Duration, MetricEvent.Level> levelFn;

    public BpTimer(final MetricName mName, final MetricClass mClass) {
        super(mName, mClass, mType);
        this.timer = new Timer();
    }

    public void update(final long duration, final TimeUnit timeUnit) {
        timer.update(duration, timeUnit);
    }

    public <T> T time(final Callable<T> event) throws Exception {

        final StopWatch sw = time();

        final T result = event.call();

        sw.stop();

        return result;
    }

    /**
     * Start a new stopwatch.
     *
     * @return A new, started stopwatch instance.
     */
    public StopWatch time() {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.setCompletionNotifier(elapsedMillis -> this.timer.update(elapsedMillis.toMillis(), TimeUnit.MILLISECONDS));
        stopWatch.setLapNotifier(this::emitLapEvent);

        stopWatch.start();

        return stopWatch;
    }

    public void setEventRecordLevelFn(final Function<Duration, MetricEvent.Level> fn) {
        this.levelFn = fn;
    }

    private void emitLapEvent(final Optional<MetricContext> context, final Duration elapsed) {
        emitEvent(context, new Measurement<>(elapsed.toMillis(), Measurement.Units.MILLISECONDS), determineLevel(elapsed));
    }

    private MetricEvent.Level determineLevel(final Duration elapsed) {
        if (levelFn != null) {
            return levelFn.apply(elapsed);
        } else {
            return MetricEvent.Level.INFO;
        }
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

}
