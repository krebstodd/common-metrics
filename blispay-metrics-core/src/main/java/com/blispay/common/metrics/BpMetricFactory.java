package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHealthCheck;
import com.blispay.common.metrics.metric.BpHistogram;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.metric.BpTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

import static com.codahale.metrics.MetricRegistry.name;

public class BpMetricFactory {

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricFactory.class);

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

        return new BpGauge<>(name(clazz.getName(), metricName), description, resultSupplier);
    }

    public BpHealthCheck createHealthCheck(final Class clazz, final String metricName,
                                           final String description, final Supplier<BpHealthCheck.Result> healthSupplier) {
        return new BpHealthCheck(name(clazz.getName(), metricName), description, healthSupplier);
    }

    private <M extends BpMetric> M createMetric(final String namespace, final String metricName,
                                                final String description, final Class<M> metricClass) {

        try {
            final Constructor<M> ctor = metricClass.getConstructor(String.class, String.class);
            final String fullName = name(namespace, metricName);
            return ctor.newInstance(fullName, description);
        } catch (ReflectiveOperationException ex) {
            LOG.error("Unable to create metric", ex);
            throw new IllegalStateException(ex);
        }
    }

}
