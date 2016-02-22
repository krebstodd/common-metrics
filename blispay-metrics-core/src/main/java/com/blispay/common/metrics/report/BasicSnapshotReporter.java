package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.BaseMetricModel;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BasicSnapshotReporter implements SnapshotReporter {

    private Supplier<Set<SnapshotProvider>> providerSupplier;

    @Override
    public Set<BaseMetricModel> report() {
        return providerSupplier.get().stream()
                .map(snProvider -> snProvider.snapshot())
                .collect(Collectors.toSet());
    }

    @Override
    public void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers) {
        this.providerSupplier = providers;
    }

}
