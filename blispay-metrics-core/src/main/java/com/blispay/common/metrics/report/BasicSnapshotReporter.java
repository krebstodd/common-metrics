package com.blispay.common.metrics.report;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BasicSnapshotReporter implements SnapshotReporter {

    private Supplier<Set<SnapshotProvider>> providerSupplier;

    @Override
    public Snapshot report() {
        return new Snapshot(providerSupplier.get().stream()
                .map(snProvider -> snProvider.snapshot())
                .collect(Collectors.toSet()));
    }

    @Override
    public void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers) {
        this.providerSupplier = providers;
    }

}
