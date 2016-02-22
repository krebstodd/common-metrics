package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public abstract class ResourceCallTimer<C extends ResourceCallTimer.Context> extends MetricRepository {

    public ResourceCallTimer(final EventEmitter emitter) {
        super(emitter);
    }

    protected StopWatch start(final C context) {
        return new StopWatch(context).start();
    }

    protected abstract BaseMetricModel buildEvent(final C context);

    public void accept(final C context) {
        save(buildEvent(context));
    }

    protected static class Context {

        private final Direction direction;
        private final UserTrackingInfo trackingInfo;
        private Status status;
        private Duration duration;

        public Context(final Direction direction, final UserTrackingInfo trackingInfo) {

            this.direction = direction;
            this.trackingInfo = trackingInfo;
        }

        public void setStatus(final Status status) {
            this.status = status;
        }

        public void setDuration(final Duration duration) {
            this.duration = duration;
        }

        public Direction getDirection() {
            return direction;
        }

        public UserTrackingInfo getTrackingInfo() {
            return trackingInfo;
        }

        public Status getStatus() {
            return status;
        }

        public Duration getDuration() {
            return duration;
        }
    }

    public class StopWatch implements AutoCloseable {

        private BiConsumer<C, Duration> completionNotifier;
        private Long startMillis;
        private AtomicBoolean isRunning = new AtomicBoolean(false);

        private C callContext;

        public StopWatch(final C callContext) {
            this.callContext = callContext;
        }

        /**
         * Start the stopwatch.
         */
        public StopWatch start() {
            if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
                startMillis = currMillis();
                return this;
            } else {
                throw new IllegalStateException("Stopwatch already started.");
            }
        }

        public Duration stop(final Status callStatus) {
            assertRunning(Boolean.TRUE);

            final Duration elapsed = Duration.ofMillis(elapsedMillis());
            this.callContext.setStatus(callStatus);
            this.callContext.setDuration(elapsed);
            ResourceCallTimer.this.accept(this.callContext);

            isRunning.set(Boolean.FALSE);
            return elapsed;
        }

        public Boolean isRunning() {
            return isRunning.get();
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

        @Override
        public void close() {
            stop(Status.success());
        }

    }

}
