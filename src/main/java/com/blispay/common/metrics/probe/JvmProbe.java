package com.blispay.common.metrics.probe;

import com.blispay.common.metrics.BpCounter;
import com.blispay.common.metrics.BpGauge;
import com.blispay.common.metrics.BpHistogram;
import com.blispay.common.metrics.BpMeter;
import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricService;
import com.blispay.common.metrics.BpTimer;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class JvmProbe {

    private static final AtomicBoolean jvmProbStarted = new AtomicBoolean(false);

    public static void startJvmProbe(final BpMetricService metricService) {
        if (jvmProbStarted.compareAndSet(false, true)) {
            metricService.addAll(wrapMetrics("memory", new MemoryUsageGaugeSet()));
            metricService.addAll(wrapMetrics("gc", new GarbageCollectorMetricSet()));
            metricService.addAll(wrapMetrics("thread", new ThreadStatesGaugeSet()));
        }
    }

    private static final List<BpMetric> wrapMetrics(String prefix, final MetricSet raw) {
        final Map<String, Metric> rawMetrics = raw.getMetrics();

        List<BpMetric> metrics = new ArrayList<>(rawMetrics.size());
        final Iterator iter = rawMetrics.keySet().iterator();
        while(iter.hasNext()) {
            final String current = (String) iter.next();
            metrics.add(wrapMetric(rawMetrics.get(current), name(prefix, current)));
        }

        return metrics;
    }

    private static final BpMetric wrapMetric(final Metric metric, final String name) {
        if (metric instanceof Timer) {
            return new BpTimer((Timer) metric, name, "");
        } else if (metric instanceof Gauge) {
            return new BpGauge((Gauge) metric, name, "");
        } else if (metric instanceof Counter) {
            return new BpCounter((Counter) metric, name, "");
        } else if (metric instanceof Meter) {
            return new BpMeter((Meter) metric, name, "");
        } else if (metric instanceof Histogram) {
            return new BpHistogram((Histogram) metric, name, "");
        } else {
            return null;
        }
    }

    private static final String name(final String prefix, final String metricNAme) {
        return JvmProbe.class.getName() + "." + prefix + "." + metricNAme;
    }

}
