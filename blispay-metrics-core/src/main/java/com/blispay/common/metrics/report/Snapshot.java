package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.util.Collections;
import java.util.Set;

public class Snapshot {

    private final Set<EventModel> metrics;

    public Snapshot(final Set<EventModel> metrics) {
        this.metrics = Collections.unmodifiableSet(metrics);
    }

    public Set<EventModel> getMetrics() {
        return metrics;
    }

}
