package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.util.ImmutablePair;

import java.util.function.Supplier;

public class BpHealthCheck extends BpMetric {

    private final Supplier<Result> healthGauge;

    public BpHealthCheck(final String name, final String description, final Supplier<Result> healthGauge) {
        super(name, description);
        this.healthGauge = healthGauge;
    }

    // CHECK_OFF: MagicNumber
    @Override
    public Sample sample() {
        final Result result = healthGauge.get();

        final ImmutablePair[] sample = new ImmutablePair[5];
        sample[0] = new ImmutablePair("name", getName());
        sample[1] = new ImmutablePair("description", getDescription());
        sample[2] = new ImmutablePair("healthy", result.isHealthy());
        sample[3] = new ImmutablePair("message", result.getMessage());
        sample[4] = new ImmutablePair("throwable", result.getThrowable());

        return new Sample(getName(), sample);
    }
    // CHECK_ON: MagicNumber

    public static final class Result {

        private final boolean isHealthy;

        private final String message;

        private final Throwable throwable;

        private Result(final boolean isHealthy, final String message, final Throwable error) {
            this.isHealthy = isHealthy;
            this.message = message;
            this.throwable = error;
        }

        public boolean isHealthy() {
            return isHealthy;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public static Result unhealthy(final String message, final Throwable throwable) {
            return new Result(false, message, throwable);
        }

        public static Result unhealthy(final Throwable throwable) {
            return unhealthy(null, throwable);
        }

        public static Result unhealthy(final String message) {
            return unhealthy(message, null);
        }

        public static Result unhealthy() {
            return unhealthy(null, null);
        }

        public static Result healthy() {
            return new Result(true, null, null);
        }
    }

}
