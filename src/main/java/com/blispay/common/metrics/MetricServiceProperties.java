package com.blispay.common.metrics;

public final class MetricServiceProperties {

    private static final String DEFAULT_JMX_ENABLED = "false";

    private static final String DEFAULT_SLF4J_ENABLED = "false";

    private static final String DEFAULT_JVM_PROBE_ENABLED = "true";

    private static final String DEFAULT_SLF4J_LOGGER_NAME = "METRICS-LOGGER";

    private static final String DEFAULT_SLF4J_LOGGER_PERIOD = "60";

    private static final String DEFAULT_SLF4J_LOGGER_UNIT = "SECONDS";

    public static final Boolean jmxReportingEnabled() {
        return Boolean.valueOf((String) System.getProperties().getOrDefault("metrics.jmx.enabled", DEFAULT_JMX_ENABLED));
    }

    public static final Boolean slf4jReportingEnabled() {
        return Boolean.valueOf((String) System.getProperties().getOrDefault("metrics.slf4j.enabled", DEFAULT_SLF4J_ENABLED));
    }

    public static final Boolean jvmProbeEnabled() {
        return Boolean.valueOf((String) System.getProperties().getOrDefault("metrics.jvm.enabled", DEFAULT_JVM_PROBE_ENABLED));
    }

    public static final String slf4jLogger() {
        return (String) System.getProperties().getOrDefault("metrics.slf4j.logger", DEFAULT_SLF4J_LOGGER_NAME);
    }

    public static final String slf4jLogPeriod() {
        return (String) System.getProperties().getOrDefault("metrics.slf4j.period", DEFAULT_SLF4J_LOGGER_PERIOD);
    }

    public static final String slf4jLogPeriodUnit() {
        return (String) System.getProperties().getOrDefault("metrics.slf4j.unit", DEFAULT_SLF4J_LOGGER_UNIT);
    }
}
