package com.blispay.common.metrics;

public final class BpMetricServiceProperties {

    /**
     * Expected system property key.
     */
    public static final String JMX_REPORTING_ENABLED_PROP = "metrics.jmx.enabled";

    /**
     * Expected system property key.
     */
    public static final String SLF4J_REPORTING_ENABLED_PROP = "metrics.slf4j.enabled";

    /**
     * Expected system property key.
     */
    public static final String SLF4J_LOGGER_NAME_PROP = "metrics.slf4j.logger";

    /**
     * Expected system property key.
     */
    public static final String SLF4J_LOG_PERIOD_PROP = "metrics.slf4j.period";

    /**
     * Expected system property key.
     */
    public static final String SLF4J_LOG_PERIOD_UNIT_PROP = "metrics.slf4j.unit";

    private static final String DEFAULT_JMX_ENABLED = "false";

    private static final String DEFAULT_SLF4J_ENABLED = "false";

    private static final String DEFAULT_SLF4J_LOGGER_NAME = "METRICS-LOGGER";

    private static final String DEFAULT_SLF4J_LOGGER_PERIOD = "60";

    private static final String DEFAULT_SLF4J_LOGGER_UNIT = "SECONDS";

    private BpMetricServiceProperties() {}

    public static Boolean jmxReportingEnabled() {
        return Boolean.valueOf((String) System.getProperties().getOrDefault("metrics.jmx.enabled", DEFAULT_JMX_ENABLED));
    }

    public static Boolean slf4jReportingEnabled() {
        return Boolean.valueOf((String) System.getProperties().getOrDefault("metrics.slf4j.enabled", DEFAULT_SLF4J_ENABLED));
    }

    public static String slf4jLogger() {
        return (String) System.getProperties().getOrDefault("metrics.slf4j.logger", DEFAULT_SLF4J_LOGGER_NAME);
    }

    public static String slf4jLogPeriod() {
        return (String) System.getProperties().getOrDefault("metrics.slf4j.period", DEFAULT_SLF4J_LOGGER_PERIOD);
    }

    public static String slf4jLogPeriodUnit() {
        return (String) System.getProperties().getOrDefault("metrics.slf4j.unit", DEFAULT_SLF4J_LOGGER_UNIT);
    }
}
