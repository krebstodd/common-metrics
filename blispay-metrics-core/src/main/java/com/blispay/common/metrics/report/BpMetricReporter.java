package com.blispay.common.metrics.report;

import com.blispay.common.metrics.metric.BpMetric;

import java.util.Map;
import java.util.function.Supplier;

public abstract class BpMetricReporter {

    private Supplier<Map<String, BpMetric.Sample>> sampler;

    public void setSampler(final Supplier<Map<String, BpMetric.Sample>> sampler) {
        this.sampler = sampler;
    }

    protected Map<String, BpMetric.Sample> sampleMetrics() {
        return sampler.get();
    }

    public abstract void start();

    public abstract void stop();

    public abstract void report();

}
