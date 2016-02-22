package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.BaseMetricModel;

import java.util.Set;
import java.util.function.Supplier;

public interface SnapshotReporter {

    public abstract Set<BaseMetricModel> report();

    public abstract void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers);

}
