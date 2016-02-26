package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ResourceCallTimer<C extends ResourceCallTimer.Context> extends MetricRepository {

    public ResourceCallTimer(final MetricGroup group, final String name, final EventEmitter emitter) {
        super(group, name, emitter);
    }

    protected StopWatch start(final C context) {
        return new StopWatchImpl(context).start();
    }

    protected abstract BaseMetricModel buildEvent(final C context);

    public void accept(final C context) {
        save(buildEvent(context));
    }

    public static StopWatch mockStopWatch() {
        return new StopWatch() {
            @Override
            public Duration stop(final Status callStatus) {
                return Duration.ofSeconds(0);
            }

            @Override
            public Boolean isRunning() {
                return false;
            }

            @Override
            public Long elapsedMillis() {
                return 0L;
            }
        };
    }

    protected static class Context {

        private final Direction direction;
        private Status status;
        private Duration duration;

        protected Context(final Direction direction) {
            this.direction = direction;
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

        public Status getStatus() {
            return status;
        }

        public Duration getDuration() {
            return duration;
        }
    }

    public interface StopWatch extends AutoCloseable {

        public Duration stop(final Status callStatus);

        public Boolean isRunning();

        public Long elapsedMillis();

        @Override
        default void close() {
            stop(Status.success());
        }

    }

    protected class StopWatchImpl implements StopWatch {

        private Long startMillis;
        private AtomicBoolean isRunning = new AtomicBoolean(false);

        private C callContext;

        public StopWatchImpl(final C callContext) {
            this.callContext = callContext;
        }

        /**
         * Start the stopwatch.
         *
         * @return Running stopwatch instance.
         */
        public StopWatchImpl start() {
            if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
                startMillis = currMillis();
                return this;
            } else {
                throw new IllegalStateException("Stopwatch already started.");
            }
        }

        /**
         * Stop the currently running snapshot. Forces the timer to emit a resource call event.
         *
         * @param callStatus The status of the call response.
         * @return The duration of the call execution.
         */
        @Override
        public Duration stop(final Status callStatus) {
            assertRunning(Boolean.TRUE);

            final Duration elapsed = Duration.ofMillis(elapsedMillis());
            this.callContext.setStatus(callStatus);
            this.callContext.setDuration(elapsed);
            ResourceCallTimer.this.accept(this.callContext);

            isRunning.set(Boolean.FALSE);
            return elapsed;
        }

        @Override
        public Boolean isRunning() {
            return isRunning.get();
        }

        @Override
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

    }

}
