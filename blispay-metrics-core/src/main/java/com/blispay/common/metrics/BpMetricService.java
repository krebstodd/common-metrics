package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHealthCheck;
import com.blispay.common.metrics.metric.BpHistogram;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.probe.BpMetricProbe;
import com.blispay.common.metrics.report.BpMetricReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class BpMetricService {

    private static BpMetricService GLOBAL_INSTANCE = new BpMetricService();

    private final BpMetricFactory metricFactory = new BpMetricFactory();

    private final BpMetricSet metrics = new BpMetricSet();

    private final List<BpMetricProbe> probes = new ArrayList<>();

    private final List<BpMetricReporter> reporters = new ArrayList<>();

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public BpMetricService() {
    }

    public BpCounter createCounter(final Class clazz, final String metricName, final String description) {
        return registerNewMetric(metricFactory.createCounter(clazz, metricName, description));
    }

    public BpHistogram createHistogram(final Class clazz, final String metricName, final String description) {
        return registerNewMetric(metricFactory.createHistogram(clazz, metricName, description));
    }

    public BpMeter createMeter(final Class clazz, final String metricName, final String description) {
        return registerNewMetric(metricFactory.createMeter(clazz, metricName, description));
    }

    public BpTimer createTimer(final Class clazz, final String metricName, final String description) {
        return registerNewMetric(metricFactory.createTimer(clazz, metricName, description));
    }

    public <R> BpGauge<R> createGauge(final Class clazz, final String metricName,
                                      final String description, final Supplier<R> resultSupplier) {

        return registerNewMetric(metricFactory.createGauge(clazz, metricName, description, resultSupplier));
    }

    public BpHealthCheck createHealthCheck(final Class clazz, final String metricName,
                                           final String description, final Supplier<BpHealthCheck.Result> healthSupplier) {
        return registerNewMetric(metricFactory.createHealthCheck(clazz, metricName, description, healthSupplier));
    }

    public BpMetric getMetric(final Class owningClass, final String metricName) {
        return metrics.getMetric(owningClass, metricName);
    }

    public void addAll(final Collection<BpMetric> metrics) {
        metrics.forEach(this::registerNewMetric);
    }

    public void removeMetric(final BpMetric metric) {
        metrics.removeMetric(metric.getName());
    }

    public void clear() {
        metrics.clear();
    }

    private <M extends BpMetric> M registerNewMetric(final M metric) {
        return metrics.registerMetric(metric);
    }

    /**
     * Add a new probe instance. Will start the probe if the service is already running.
     * @param probe A new probe using the current metric service instance.
     */
    public void addProbe(final BpMetricProbe probe) {
        probes.add(probe);

        synchronized (isRunning) {
            if (isRunning.get()) {
                probe.start();
            }
        }
    }

    /**
     * Add a new reporter to the metric service.
     * @param reporter The reporter to add.
     */
    public void addReporter(final BpMetricReporter reporter) {
        reporter.setSampler(metrics::sample);

        synchronized (isRunning) {
            if (isRunning.get()) {
                reporter.start();
            }
        }

        reporters.add(reporter);
    }

    public void removeRepoerter(final BpMetricReporter reporter) {
        reporters.remove(reporter);
    }

    /**
     * Start the bp metric service and all reporters.
     * @return The current instance.
     */
    public BpMetricService start() {
        if (!isRunning.compareAndSet(false, true)) {
            throw new IllegalStateException("Metric service is already running and cannot be started.");
        }

        reporters.forEach(BpMetricReporter::start);
        probes.forEach(BpMetricProbe::start);
        return this;
    }

    /**
     * Stop the metric service from reporting metrics.
     */
    public void stop() {
        if (!isRunning.compareAndSet(true, false)) {
            throw new IllegalStateException("Metric service is stopped.");
        }

        reporters.forEach(BpMetricReporter::stop);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void flushReporters() {
        reporters.forEach(BpMetricReporter::report);
    }

    public static BpMetricService globalInstance() {
        return GLOBAL_INSTANCE;
    }

}
