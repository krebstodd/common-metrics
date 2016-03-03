package com.blispay.common.metrics.model.health;

public class HealthCheckData {

    private final Boolean isHealthy;

    private final String message;

    private final String throwable;

    public HealthCheckData(final boolean isHealthy) {
        this(isHealthy, null, null);
    }

    public HealthCheckData(final boolean isHealthy, final String message) {
        this(isHealthy, message, null);
    }

    /**
     * The object under monitoring is currenlty unhealthy with a message and corresponding exception.
     *
     * @param isHealthy The object is healthy.
     * @param message The message to display.
     * @param error An exception that generated the unhealthy state.
     */
    public HealthCheckData(final boolean isHealthy, final String message, final Throwable error) {
        this.isHealthy = isHealthy;
        this.message = message;

        if (error != null) {
            this.throwable = error.getClass().getCanonicalName();
        } else {
            this.throwable = null;
        }
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public String getMessage() {
        return message;
    }

    public String getThrowable() {
        return throwable;
    }

    public static HealthCheckData healthy() {
        return new HealthCheckData(Boolean.TRUE);
    }

    public static HealthCheckData unHealthy() {
        return new HealthCheckData(Boolean.FALSE);
    }

    public static HealthCheckData unHealthy(final String msg) {
        return new HealthCheckData(Boolean.FALSE, msg);
    }

    public static HealthCheckData unHealthy(final Exception ex) {
        return new HealthCheckData(Boolean.FALSE, null, ex);
    }

    /**
     * The object under monitoring is currenlty unhealthy with a message and corresponding exception.
     *
     * @param message The message to display.
     * @param ex An exception that generated the unhealthy state.
     * @return Health check data.
     */
    public static HealthCheckData unHealthy(final String message, final Exception ex) {
        return new HealthCheckData(Boolean.FALSE, message, ex);
    }
}
