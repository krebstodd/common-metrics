package com.blispay.common.metrics;

import com.codahale.metrics.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class BpMetricReportingService {

    public static final Boolean DEFAULT_JMX_ENABLED = false;

    public static final Boolean DEFAULT_SLF4J_ENABLED = false;

    public static final String DEFAULT_SLF4J_LOGGER_NAME = "METRICS-LOGGER";

    public static final String DEFAULT_SLF4J_LOGGER_PERIOD = "60";

    public static final String DEFAULT_SLF4J_LOGGER_UNIT = "SECONDS";

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricReportingService.class);

    private final List<BpMetricConsumer> reporters = new ArrayList<>();

    private BpMetricReportingService() { }

    public void addConsumer(final BpMetricConsumer consumer) {
        this.reporters.add(consumer);
    }

    public void start() {
        applyToAll(BpMetricConsumer::start);
    }

    public void stop() {
        applyToAll(BpMetricConsumer::stop);
    }

    public void onMetricAdded(final BpMetric metric) {
        applyToAll((reporter) -> reporter.registerMetric(metric));
    }

    public void onMetricRemoved(final String name) {
        applyToAll((reporter) -> reporter.unregisterMetric(name));
    }

    private void applyToAll(Consumer<BpMetricConsumer> consumer) {
        synchronized (reporters) {
            for (final Iterator<BpMetricConsumer> iter = reporters.iterator(); iter.hasNext(); ) {
                consumer.accept(iter.next());
            }
        }
    }

    public static BpMetricReportingService initialize(final BpMetricService service) {

        final BpMetricReportingService reportingService = new BpMetricReportingService();

        final Properties sysProps = System.getProperties();
        final Boolean jmxEnabled = (Boolean) sysProps.getOrDefault("metrics.jmx.enabled", DEFAULT_JMX_ENABLED);
        final Boolean slf4jEnabled = (Boolean) sysProps.getOrDefault("metrics.slf4j.enabled", DEFAULT_SLF4J_ENABLED);

        if (jmxEnabled) {
            reportingService.addConsumer(buildJmxReporter(service));
        }

        if (slf4jEnabled) {
            final String loggerName = (String) sysProps.getOrDefault("metrics.slf4j.logger", DEFAULT_SLF4J_LOGGER_NAME);
            final String period = (String) sysProps.getOrDefault("metrics.slf4j.period", DEFAULT_SLF4J_LOGGER_PERIOD);
            final String unit = (String) sysProps.getOrDefault("metrics.slf4j.unit", DEFAULT_SLF4J_LOGGER_UNIT);

            reportingService.addConsumer(buildSlf4jReporter(service, loggerName, period, unit));
        }

        return reportingService;
    }

    private static BpSlf4jReporter buildSlf4jReporter(final BpMetricSet metricSet, final String loggerName,
                                                      final String period, final String unit) {
        final BpSlf4jReporter reporter = new BpSlf4jReporter(loggerName, Integer.valueOf(period), TimeUnit.valueOf(unit));
        return reporter;
    }

    private static BpJmxReporter buildJmxReporter(final BpMetricSet metricSet) {
        final BpJmxReporter reporter = new BpJmxReporter();

        // Add any already existing metrics to the jmx reporter.
        final Iterator iter = metricSet.getMetrics().values().iterator();
        while (iter.hasNext()) {
            final Metric metric = (Metric) iter.next();
            if (metric instanceof BpMetric) {
                reporter.registerMetric((BpMetric) metric);
            } else {
                LOG.warn("Cannot add non-bp metric {} to jmx metrics service", metric.getClass());
            }
        }

        return reporter;
    }
}
