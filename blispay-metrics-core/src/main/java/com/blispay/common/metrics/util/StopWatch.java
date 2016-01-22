package com.blispay.common.metrics.util;

import com.blispay.common.metrics.metric.BpTimer.Resolver;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StopWatch implements Closeable, Resolver {

    private static final String DEFAULT_COMPLETE_MSG = "COMPLETE";

    private static final String DEFAULT_LAP_MSG = "LAP";

    private static Consumer<Long> NO_OP = (val) -> { };

    private static BiConsumer<String, Long> BI_NO_OP = (str, val) -> { };

    private Consumer<Long> completionNotifier;

    private BiConsumer<String, Long> lapNotifier;

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

    public Long lap() {
        return lap(DEFAULT_LAP_MSG);
    }

    /**
     * Hit the lap button on the stopwatch.
     *
     * @param message The message to provide the lap notifier.
     * @return Milliseconds since start.
     */
    public Long lap(final String message) {
        assertRunning(Boolean.TRUE);

        final Long elapsed = elapsedMillis();
        lapNotifier.accept(message, elapsed);
        return elapsed;
    }

    public Long stop() {
        return stop(DEFAULT_COMPLETE_MSG);
    }

    /**
     * Stop the watch using hte provided message to notify lap and completion.
     *
     * @param message Message to privde in the final lap.
     * @return Milliseconds since start.
     */
    public Long stop(final String message) {
        assertRunning(Boolean.TRUE);

        final Long elapsed = elapsedMillis();
        lapNotifier.accept(message, elapsed);
        completionNotifier.accept(elapsed);

        isRunning.set(Boolean.FALSE);
        return elapsed;
    }

    public Long getStartTimeMillis() {
        assertRunning(Boolean.TRUE);
        return startMillis;
    }

    public AtomicBoolean isRunning() {
        return isRunning;
    }

    public void setCompletionNotifier(final Consumer<Long> completionNotifier) {
        this.completionNotifier = completionNotifier;
    }

    public void setLapNotifier(final BiConsumer<String, Long> lapNotifier) {
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

    @Override
    public void done() {
        stop();
    }
}
