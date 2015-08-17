package com.blispay.common.metrics;

import com.blispay.common.metrics.reporter.BpJmxReporter;
import com.blispay.common.metrics.reporter.BpSlf4jReporter;
import com.codahale.metrics.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class BpMetricReportingService {

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

    private void applyToAll(final Consumer<BpMetricConsumer> consumer) {
        synchronized (reporters) {
            for (final Iterator<BpMetricConsumer> iter = reporters.iterator(); iter.hasNext();) {
                consumer.accept(iter.next());
            }
        }
    }

    /**
     * Initialize a new metrics reporting service for the provided metric service. The reporting service will self-register
     * any default consumers that are configured via sys props.
     *
     * @param service Active metric service.
     * @return Instance of reporting service configured.
     */
    public static BpMetricReportingService initialize(final BpMetricService service) {

        final BpMetricReportingService reportingService = new BpMetricReportingService();

        final Boolean jmxEnabled = BpMetricServiceProperties.jmxReportingEnabled();
        final Boolean slf4jEnabled = BpMetricServiceProperties.slf4jReportingEnabled();

        if (jmxEnabled) {
            reportingService.addConsumer(buildJmxReporter(service));
        }

        if (slf4jEnabled) {
            final String loggerName = BpMetricServiceProperties.slf4jLogger();
            final String period = BpMetricServiceProperties.slf4jLogPeriod();
            final String unit = BpMetricServiceProperties.slf4jLogPeriodUnit();

            reportingService.addConsumer(buildSlf4jReporter(service, loggerName, period, unit));
        }

        return reportingService;
    }

    private static BpSlf4jReporter buildSlf4jReporter(final BpMetricSet metricSet, final String loggerName,
                                                      final String period, final String unit) {
        return (BpSlf4jReporter) seedConsumer(new BpSlf4jReporter(loggerName, Integer.valueOf(period), TimeUnit.valueOf(unit)), metricSet);
    }

    private static BpJmxReporter buildJmxReporter(final BpMetricSet metricSet) {
        return (BpJmxReporter) seedConsumer(new BpJmxReporter(), metricSet);
    }

    private static BpMetricConsumer seedConsumer(final BpMetricConsumer consumer, final BpMetricSet activeMetrics) {
        // Add any already existing metrics to the jmx reporter.
        final Iterator iter = activeMetrics.getMetrics().values().iterator();
        while (iter.hasNext()) {
            final Metric metric = (Metric) iter.next();
            if (metric instanceof BpMetric) {
                consumer.registerMetric((BpMetric) metric);
            } else {
                LOG.warn("Cannot add non-bp metric {} to jmx metrics service", metric.getClass());
            }
        }
        return consumer;
    }
}
