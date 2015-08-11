package com.blispay.common.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class BpMetricReportingService implements BpMetricListener {

    public static final Boolean DEFAULT_JMX_ENABLED = false;

    public static final Boolean DEFAULT_SLF4J_ENABLED = false;

    public static final String DEFAULT_SLF4J_LOGGER_NAME = "METRICS-LOGGER";

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricReportingService.class);

    private final List<BpMetricReporter> reporters = new ArrayList<>();

    private BpMetricReportingService(final BpJmxReporter jmxReporter, final BpSlf4jReporter slf4jReporter) {
        this.reporters.add(jmxReporter);
        this.reporters.add(slf4jReporter);
    }

    @Override
    public void onGaugeAdded(final String s, final Gauge<?> gauge) {

    }

    @Override
    public void onGaugeRemoved(final String s) {

    }

    @Override
    public void onCounterAdded(final BpCounter counter) {

    }

    @Override
    public void onCounterRemoved(final String s) {

    }

    @Override
    public void onHistogramAdded(final BpHistogram histogram) {

    }

    @Override
    public void onHistogramRemoved(final String s) {

    }

    @Override
    public void onMeterAdded(final BpMeter meter) {

    }

    @Override
    public void onMeterRemoved(final String s) {

    }

    @Override
    public void onTimerAdded(final BpTimer timer) {

    }

    @Override
    public void onTimerRemoved(final String s) {

    }

    public void stop() {
        applyToAll(BpMetricReporter::stop);
    }

    @Override
    public void start() {
        applyToAll(BpMetricReporter::start);
    }

    private void onMetricAdded(final BpMetric metric) {
        applyToAll((reporter) -> reporter.registerMetric(metric));
    }

    private void onMetricRemoved(final String name) {
        applyToAll((reporter) -> reporter.unregisterMetric(name));
    }

    private void applyToAll(Consumer<BpMetricReporter> consumer) {
        synchronized (reporters) {
            for (final Iterator<BpMetricReporter> iter = reporters.iterator(); iter.hasNext(); ) {
                consumer.accept(iter.next());
            }
        }
    }

    public static BpMetricReportingService initialize(final BpMetricService service) {

        BpJmxReporter jmxReporter = null;
        BpSlf4jReporter slf4jReporter = null;

        final Properties sysProps = System.getProperties();
        final Boolean jmxEnabled = (Boolean) sysProps.getOrDefault("metrics.jmx.enabled", DEFAULT_JMX_ENABLED);
        final Boolean slf4jEnabled = (Boolean) sysProps.getOrDefault("metrics.slf4j.enabled", DEFAULT_SLF4J_ENABLED);

        if (jmxEnabled) {
            jmxReporter = buildJmxReporter(service);
        }

        if (slf4jEnabled) {

        }

        return new BpMetricReportingService(null, null);
    }

    private static BpJmxReporter buildJmxReporter(final MetricSet metricSet) {
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
