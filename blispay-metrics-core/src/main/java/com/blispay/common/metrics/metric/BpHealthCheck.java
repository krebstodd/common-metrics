package com.blispay.common.metrics.metric;

import java.util.function.Supplier;

public class BpHealthCheck extends BpMetric {

    private static final MetricType mType = MetricType.HEALTH;

    private final Supplier<Result> healthGauge;

    public BpHealthCheck(final MetricName mName, final MetricClass mClass, final Supplier<Result> healthGauge) {
        super(mName, mClass, mType);

        this.healthGauge = healthGauge;
    }

    public Result checkHealth() {
        return healthGauge.get();
    }

    public static final class Result {

        private final Boolean isHealthy;

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
