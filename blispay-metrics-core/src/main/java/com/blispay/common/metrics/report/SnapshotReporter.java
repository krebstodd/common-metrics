package com.blispay.common.metrics.report;

import com.blispay.common.metrics.event.MetricEvent;

import java.util.Set;
import java.util.function.Supplier;

public abstract class SnapshotReporter {

    public void start() {

    }

    public void stop() {

    }

    public abstract Set<MetricEvent> report();

    public abstract void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers);

}
