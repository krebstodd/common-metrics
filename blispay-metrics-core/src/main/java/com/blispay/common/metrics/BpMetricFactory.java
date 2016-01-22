package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHealthCheck;
import com.blispay.common.metrics.metric.BpHistogram;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.report.BpEventService;
import com.blispay.common.metrics.report.NoOpEventReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

import static com.codahale.metrics.MetricRegistry.name;

public class BpMetricFactory {

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricFactory.class);

    private BpEventService eventService;

    public BpMetricFactory() {
        this.eventService = new NoOpEventReportingService();
    }

    public void setEventRecordingService(final BpEventService service) {
        this.eventService = service;
    }

    public BpCounter createCounter(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz, metricName, description, BpCounter.class);
    }

    public BpHistogram createHistogram(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz, metricName, description, BpHistogram.class);
    }

    public BpMeter createMeter(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz, metricName, description, BpMeter.class);
    }

    public BpTimer createTimer(final Class clazz, final String metricName, final String description) {
        return createMetric(clazz, metricName, description, BpTimer.class);
    }

    public <R> BpGauge<R> createGauge(final Class clazz, final String metricName,
                                      final String description, final Supplier<R> resultSupplier) {

        return new BpGauge<>(clazz, name(clazz, metricName), description, resultSupplier);
    }

    public BpHealthCheck createHealthCheck(final Class clazz, final String metricName,
                                           final String description, final Supplier<BpHealthCheck.Result> healthSupplier) {
        return new BpHealthCheck(clazz, name(clazz, metricName), description, healthSupplier);
    }

    private <M extends BpMetric> M createMetric(final Class<?> owner, final String metricName,
                                                final String description, final Class<M> metricClass) {

        try {
            final Constructor<M> ctor = metricClass.getConstructor(Class.class, String.class, String.class);
            final String fullName = name(owner.getName(), metricName);

            final M metric = ctor.newInstance(owner, fullName, description);
            metric.setEventService(this.eventService);
            return metric;

        } catch (ReflectiveOperationException ex) {
            LOG.error("Unable to create metric", ex);
            throw new IllegalStateException(ex);
        }
    }

}
