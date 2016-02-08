package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHealthCheck;
import com.blispay.common.metrics.metric.BpHistogram;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.probe.BpMetricProbe;
import com.blispay.common.metrics.report.BpEventListener;
import com.blispay.common.metrics.report.BpEventReporter;
import com.blispay.common.metrics.report.BpEventService;
import com.blispay.common.metrics.report.BpMetricReporter;
import com.blispay.common.metrics.report.DefaultBpEventReportingService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class BpMetricService {

    private static BpMetricService GLOBAL_INSTANCE = new BpMetricService();

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Set<BpMetricProbe> probes = new HashSet<>();
    private final Set<BpMetricReporter> reporters = new HashSet<>();

    private final BpMetricFactory metricFactory;
    private final BpEventService eventService;
    private final BpMetricSet metrics;

    public BpMetricService() {
        this(new DefaultBpEventReportingService(), new BpMetricFactory());
    }

    public BpMetricService(final BpEventService eventRecordingService) {
        this(eventRecordingService, new BpMetricFactory());
    }

    public BpMetricService(final BpMetricFactory metricFactory) {
        this(new DefaultBpEventReportingService(), metricFactory);
    }

    /**
     * Create a new bp metric service with a user provided event recording service and metric factory.
     *
     * @param eventService Event service.
     * @param metricFactory Metric factory.
     */
    public BpMetricService(final BpEventService eventService, final BpMetricFactory metricFactory) {
        this.eventService = eventService;
        this.metricFactory = metricFactory;
        this.metrics  = new BpMetricSet();

        this.metricFactory.setEventRecordingService(this.eventService);
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

    public void removeReporter(final BpMetricReporter reporter) {
        reporters.remove(reporter);
    }

    public void addEventListener(final BpEventListener listener) {
        this.eventService.addEventListener(listener);
    }

    public void removeEventListener(final BpEventReporter listener) {
        this.eventService.removeEventListener(listener);
    }

    /**
     * Start the bp metric service and all reporters.
     * @return The current instance.
     */
    public BpMetricService start() {

        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            reporters.forEach(BpMetricReporter::start);
            probes.forEach(BpMetricProbe::start);
        }

        return this;
    }

    /**
     * Stop the metric service from reporting metrics.
     */
    public void stop() {
        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {
            reporters.forEach(BpMetricReporter::stop);
        }
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
