package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.BaseMetricModel;

import java.util.Collections;
import java.util.Set;

public class Snapshot {

    private final Set<BaseMetricModel> metrics;

    public Snapshot(final Set<BaseMetricModel> metrics) {
        this.metrics = Collections.unmodifiableSet(metrics);
    }

    public Set<BaseMetricModel> getMetrics() {
        return metrics;
    }

}
