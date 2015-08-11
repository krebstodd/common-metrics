package com.blispay.common.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.SharedMetricRegistries;

import java.lang.reflect.Constructor;
import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

public class BpMetricService implements MetricSet {

    private static BpMetricService metricsService = null;

    private static final String companyRegistryName = "blispay";

    private final String appName;

    private final String registryName;

    private final MetricRegistry registry;

    private final BpMetricReportingService reportingService;

    private BpMetricService(final String appName) {
        this.appName = appName;
        this.registryName = name(companyRegistryName, appName);
        this.registry = SharedMetricRegistries.getOrCreate(this.registryName);
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

    public void removeMetric(final BpMetric metric) {
        registry.remove(metric.getName());
    }

    public String getAppName() {
        return appName;
    }

    public Map<String, Metric> getMetrics() {
        return this.registry.getMetrics();
    }

    private <M extends BpMetric> M createMetric(final String category, final String metricName,
                                                final String description, final Class<M> metricClass) {

        try {
            final Constructor<M> ctor = metricClass.getConstructor(String.class, String.class);
            final String fullName = name(registryName, category, metricName);

            final M instance = ctor.newInstance(fullName, description);
            this.registry.register(fullName, instance);
            return instance;
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

//    private static void instrumentJvmMonitoring(final BpMetricService service) {
//        final Boolean jvmMonitoringEnabled = (Boolean) System.getProperties().getOrDefault("metrics.jvm.enabled", false);
//
//        if (jvmMonitoringEnabled) {
//            service.getRegistry().registerAll(new MemoryUsageGaugeSet());
//            service.getRegistry().registerAll(new GarbageCollectorMetricSet());
//            service.getRegistry().registerAll(new ThreadStatesGaugeSet());
//        }
//    }

}
