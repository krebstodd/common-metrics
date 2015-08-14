package com.blispay.common.metrics;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.context.Lifecycle;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.codahale.metrics.MetricRegistry.name;

// TODO: Create metric sets for groupings (api, query, etc.) and possibly probes.
// TODO: Look at the units that metrics are coming back in.
//    private static void instrumentJvmMonitoring(final BpMetricService service) {
//        final Boolean jvmMonitoringEnabled = (Boolean) System.getProperties().getOrDefault("metrics.jvm.enabled", false);
//
//        if (jvmMonitoringEnabled) {
//            service.getRegistry().registerAll(new MemoryUsageGaugeSet());
//            service.getRegistry().registerAll(new GarbageCollectorMetricSet());
//            service.getRegistry().registerAll(new ThreadStatesGaugeSet());
//        }
//    }

public final class BpMetricService implements BpMetricSet, Lifecycle {

    private static final BpMetricService METRIC_SERVICE = new BpMetricService();

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricService.class);

    private final ConcurrentHashMap<String, BpMetric> metrics = new ConcurrentHashMap<>();

    private final BpMetricReportingService reportingService = BpMetricReportingService.initialize(this);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private BpMetricService() {
        start();
    }

    public BpCounter createCounter(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz.getName(), metricName, description, BpCounter.class);
    }

    public BpHistogram createHistogram(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz.getName(), metricName, description, BpHistogram.class);
    }

    public BpMeter createMeter(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz.getName(), metricName, description, BpMeter.class);
    }

    public BpTimer createTimer(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz.getName(), metricName, description, BpTimer.class);
    }

    public <R> BpGauge<R> createGauge(final Class clazz, final String metricName,
                                      final String description, final Supplier<R> resultSupplier) {

        return (BpGauge<R>) registerMetric(new BpGauge(name(clazz.getName(), metricName), description, resultSupplier));
    }

    public BpMetric getMetric(final Class owningClass, final String metricName) {
        return getMetricByFullName(name(owningClass.getName(), metricName));
    }

    public BpMetric getMetricByFullName(final String name) {
        return metrics.get(name);
    }

    private void removeMetricByFullName(final String metricName) {
        final BpMetric removed = this.metrics.remove(metricName);

        // TODO: Make thread safe.
        if (removed != null) {
            this.reportingService.onMetricRemoved(metricName);
        }
    }

    public void removeMetric(final Class owningClass, final String name) {
        removeMetricByFullName(name(owningClass.getName(), name));
    }

    public void removeMetric(final BpMetric metric) {
        removeMetricByFullName(metric.getName());
    }

    @Override
    public Map<String, BpMetric> getMetrics() {
        return this.metrics;
    }

    private <M extends BpMetric> M createMetric(final String namespace, final String metricName,
                                                final String description, final Class<M> metricClass) {

        try {

            final Constructor<M> ctor = metricClass.getConstructor(String.class, String.class);
            final String fullName = name(namespace, metricName);

            if (this.metrics.containsKey(fullName)) {
                throw new IllegalArgumentException("Metric has already been registered with name " + fullName);
            }

            // TODO: Make all of this thread safe possibly locking on the metrics object or a specific lock for writing to the map.
            return (M) registerMetric(ctor.newInstance(fullName, description));

        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {
            // Eat this exception, we know the constructor type for all of hte BpMetrics so we shouldn't ever actually
            // hit this catc.
            LOG.error("Caught an exception attempting to instantiate a metric instance.", ex);
            return null;
        }
        // CHECK_ON: IllegalCatch
    }

    private BpMetric registerMetric(final BpMetric metric) {
        this.metrics.put(metric.getName(), metric);
        this.reportingService.onMetricAdded(metric);
        return metric;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            reportingService.start();
        } else {
            throw new IllegalStateException("Metric service is already running and cannot be started.");
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            reportingService.stop();
        } else {
            throw new IllegalStateException("Metric service is stopped.");
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Get an instance of the blispay metric service. Service is a singleton to ensure the entire process is utilizing
     * the same metric set.
     *
     * @return Existing singleton.
     */
    public static BpMetricService getInstance() {
        return METRIC_SERVICE;
    }

}
