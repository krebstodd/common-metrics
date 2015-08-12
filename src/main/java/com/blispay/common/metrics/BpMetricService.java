package com.blispay.common.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codahale.metrics.MetricRegistry.name;

// TODO
//    private static void instrumentJvmMonitoring(final BpMetricService service) {
//        final Boolean jvmMonitoringEnabled = (Boolean) System.getProperties().getOrDefault("metrics.jvm.enabled", false);
//
//        if (jvmMonitoringEnabled) {
//            service.getRegistry().registerAll(new MemoryUsageGaugeSet());
//            service.getRegistry().registerAll(new GarbageCollectorMetricSet());
//            service.getRegistry().registerAll(new ThreadStatesGaugeSet());
//        }
//    }

public class BpMetricService implements BpMetricSet {

    private static BpMetricService metricsService = null;

    private static final String companyRegistryName = "blispay";

    private final String appName;

    private final String registryName;

    private final ConcurrentHashMap<String, BpMetric> metrics = new ConcurrentHashMap<>();

    private final BpMetricReportingService reportingService;

    private BpMetricService(final String appName) {
        this.appName = appName;
        this.registryName = name(companyRegistryName, appName);
        this.reportingService = BpMetricReportingService.initialize(this);
    }

    public BpCounter createCounter(final String category, final String metricName, final String description) {
        return createMetric(category, metricName, description, BpCounter.class);
    }

    public BpHistogram createHistogram(final String category, final String metricName, final String description) {
        return createMetric(category, metricName, description, BpHistogram.class);
    }

    public BpMeter createMeter(final String category, final String metricName, final String description) {
        return createMetric(category, metricName, description, BpMeter.class);
    }

    public BpTimer createTimer(final String category, final String metricName, final String description) {
        return createMetric(category, metricName, description, BpTimer.class);
    }

    public void removeMetric(final String metricName) {
        final BpMetric removed = this.metrics.remove(metricName);

        // TODO: Make thread safe.
        if (removed != null) {
            this.reportingService.onMetricRemoved(metricName);
        }
    }

    public void removeMetric(final BpMetric metric) {
        removeMetric(metric.getName());
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public Map<String, BpMetric> getMetrics() {
        return this.metrics;
    }

    private <M extends BpMetric> M createMetric(final String category, final String metricName,
                                                final String description, final Class<M> metricClass) {

        try {
            final Constructor<M> ctor = metricClass.getConstructor(String.class, String.class);
            final String fullName = name(registryName, category, metricName);

            if (this.metrics.containsKey(fullName)) {
                throw new IllegalArgumentException("Metric has already been registered with name " + fullName);
            }

            final M metric = ctor.newInstance(fullName, description);

            // TODO: Make all of this thread safe possibly locking on the metrics object or a specific lock for writing to the map.
            this.metrics.put(fullName, metric);
            this.reportingService.onMetricAdded(metric);
            return metric;
        } catch (Exception e) {
            // Eat this exception, we know the constructor type for all of hte BpMetrics
            e.printStackTrace();
            return null;
        }
    }

    public static BpMetricService getInstance(final String appName) {
        if (metricsService == null) {

            synchronized (BpMetricService.class) {
                if (metricsService == null) {
                    metricsService = new BpMetricService(appName);
                }
            }

        } else if (!appName.equals(metricsService.getAppName())) {
            throw new IllegalArgumentException("A blispay metrics service has already been created with app name " + metricsService.getAppName());
        }

        return metricsService;
    }

}
