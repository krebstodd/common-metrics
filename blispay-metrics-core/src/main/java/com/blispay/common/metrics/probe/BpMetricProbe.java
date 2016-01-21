package com.blispay.common.metrics.probe;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHistogram;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.metric.BpTimer;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BpMetricProbe {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Start the current metric probe running. Not all probe types will require logic in their start method, however most will.
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            startProbe();
        } else {
            getLogger().warn("Probe already started, will not attempt a restart");
        }
    }

    protected static BpMetric wrapMetric(final Metric plain, final String name) {
        final BpMetric metric;
        
        if (plain instanceof Timer) {
            metric = new BpTimer((Timer) plain, name, "");
        } else if (plain instanceof Gauge) {
            metric = new BpGauge((Gauge) plain, name, "");
        } else if (plain instanceof Counter) {
            metric = new BpCounter((Counter) plain, name, "");
        } else if (plain instanceof Meter) {
            metric = new BpMeter((Meter) plain, name, "");
        } else if (plain instanceof Histogram) {
            metric = new BpHistogram((Histogram) plain, name, "");
        } else {
            metric = null;
        }

        return metric;
    }

    protected abstract void startProbe();

    protected abstract Logger getLogger();
}
