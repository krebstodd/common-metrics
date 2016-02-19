package com.blispay.common.metrics.util;

import com.blispay.common.metrics.metric.MetricContext;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StopWatch implements AutoCloseable {

    private static Consumer<Duration> NO_OP = (val) -> { };
    private static BiConsumer<Optional<MetricContext>, Duration> BI_NO_OP = (str, val) -> { };

    private Consumer<Duration> completionNotifier;
    private BiConsumer<Optional<MetricContext>, Duration> lapNotifier;
    private Long startMillis;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public StopWatch() {
        this.completionNotifier = NO_OP;
        this.lapNotifier = BI_NO_OP;
    }

    /**
     * Start the stopwatch.
     */
    public void start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            startMillis = currMillis();
        } else {
            throw new IllegalStateException("Stopwatch already started.");
        }
    }

    public Duration lap() {
        return lap(Optional.empty());
    }

    public Duration lap(final MetricContext context) {
       return lap(Optional.of(context));
    }

    private Duration lap(final Optional<MetricContext> context) {
        assertRunning(Boolean.TRUE);

        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        lapNotifier.accept(context, elapsed);
        return elapsed;
    }

    public Duration stop() {
        return stop(Optional.empty());
    }

    public Duration stop(final MetricContext context) {
        return stop(Optional.of(context));
    }

    private Duration stop(final Optional<MetricContext> context) {
        assertRunning(Boolean.TRUE);

        final Duration elapsed = Duration.ofMillis(elapsedMillis());
        lapNotifier.accept(context, elapsed);
        completionNotifier.accept(elapsed);

        isRunning.set(Boolean.FALSE);
        return elapsed;
    }

    public AtomicBoolean isRunning() {
        return isRunning;
    }

    public void setCompletionNotifier(final Consumer<Duration> completionNotifier) {
        this.completionNotifier = completionNotifier;
    }

    public void setLapNotifier(final BiConsumer<Optional<MetricContext>, Duration> lapNotifier) {
        this.lapNotifier = lapNotifier;
    }

    public Long elapsedMillis() {
        assertRunning(Boolean.TRUE);
        return currMillis() - startMillis;
    }

    private Long currMillis() {
        return System.currentTimeMillis();
    }

    private void assertRunning(final boolean expected) {
        if (this.isRunning.get() != expected) {
            throw new IllegalStateException("Stopwatch not in expected state.");
        }
    }

    /**
     * Start a new stopwatch instance.
     * @return Started watch.
     */
    public static StopWatch startWatch() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        return stopWatch;
    }

    @Override
    public void close() {
        stop();
    }

}
