package com.blispay.common.metrics.metric;

import java.util.Map;

public class MappedMetricContext extends MetricContext {

    private final Map<String, String> context;

    public MappedMetricContext(final Map<String, String> context) {
        this.context = context;
    }

    @Override
    public Map<String, String> getContextMap() {
        return this.context;
    }
}
