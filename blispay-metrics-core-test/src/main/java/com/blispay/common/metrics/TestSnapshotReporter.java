package com.blispay.common.metrics;

import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.report.SnapshotReporter;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TestSnapshotReporter extends SnapshotReporter {

    private Supplier<Set<SnapshotProvider>> snapshotSupplier;

    @Override
    public Set<MetricEvent> report() {
        return snapshotSupplier.get().stream().map(SnapshotProvider::snapshot).collect(Collectors.toSet());
    }

    @Override
    public void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers) {
        this.snapshotSupplier = providers;
    }

}
