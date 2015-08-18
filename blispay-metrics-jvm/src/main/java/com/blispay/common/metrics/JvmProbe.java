package com.blispay.common.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JvmProbe extends BpMetricProbe {

    public JvmProbe() {}

    @Override
    protected void startProbe() {
        metricService.addAll(wrapMetrics("memory", new MemoryUsageGaugeSet()));
        metricService.addAll(wrapMetrics("gc", new GarbageCollectorMetricSet()));
        metricService.addAll(wrapMetrics("thread", new ThreadStatesGaugeSet()));
    }

    @Override
    protected Logger getLogger() {
        return LoggerFactory.getLogger(JvmProbe.class);
    }

    private static List<BpMetric> wrapMetrics(final String prefix, final MetricSet raw) {
        final Map<String, Metric> rawMetrics = raw.getMetrics();

        final List<BpMetric> metrics = new ArrayList<>(rawMetrics.size());
        final Iterator iter = rawMetrics.keySet().iterator();
        while (iter.hasNext()) {
            final String current = (String) iter.next();
            metrics.add(wrapMetric(rawMetrics.get(current), name(prefix, current)));
        }

        return metrics;
    }

    private static String name(final String prefix, final String metricNAme) {
        return JvmProbe.class.getName() + "." + prefix + "." + metricNAme;
    }

}
