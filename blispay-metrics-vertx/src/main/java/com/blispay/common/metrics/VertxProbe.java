package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.probe.BpMetricProbe;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import io.vertx.core.Vertx;
import io.vertx.ext.dropwizard.impl.AbstractMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VertxProbe extends BpMetricProbe {

    private static final Logger LOG = LoggerFactory.getLogger(VertxProbe.class);

    /**
     * Create a new instance of the vertx metrics probe.
     *
     * @param vertx The active vertx instance to be probed.
     * @param metricService Metric service to register probe on.
     */
    public VertxProbe(final Vertx vertx, final BpMetricService metricService) {
        final AbstractMetrics abstractMetrics = AbstractMetrics.unwrap(vertx);

        try {
            final MetricRegistry internal = getInternalRegistry(abstractMetrics);

            final Map<String, Metric> activeMetrics = internal.getMetrics();
            metricService.addAll(transformMetrics(activeMetrics));

            clearInternalRegistry(abstractMetrics);

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private MetricRegistry getInternalRegistry(final AbstractMetrics abstractMetrics) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getter = null;
        try {
            getter = AbstractMetrics.class.getDeclaredMethod("registry");
            getter.setAccessible(true);
            return (MetricRegistry) getter.invoke(abstractMetrics);
        } finally {
            if (getter != null) {
                getter.setAccessible(false);
            }
        }
    }

    private void clearInternalRegistry(final AbstractMetrics abstractMetrics) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method removeAll = null;

        try {
            removeAll = AbstractMetrics.class.getDeclaredMethod("removeAll");
            removeAll.setAccessible(true);
            removeAll.invoke(abstractMetrics);
        } finally {
            if (removeAll != null) {
                removeAll.setAccessible(false);
            }
        }
    }

    private List<BpMetric> transformMetrics(final Map<String, Metric> metrics) {
        final List<BpMetric> wrapped = new ArrayList<>(metrics.size());
        final Iterator iter = metrics.keySet().iterator();

        while (iter.hasNext()) {
            final String name = (String) iter.next();
            wrapped.add(wrapMetric(metrics.get(name), name));
        }

        return wrapped;
    }

    @Override
    protected void startProbe() {

    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
