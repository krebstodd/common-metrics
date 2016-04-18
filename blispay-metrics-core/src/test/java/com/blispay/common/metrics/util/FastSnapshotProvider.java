package com.blispay.common.metrics.util;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.report.SnapshotProvider;

public class FastSnapshotProvider implements SnapshotProvider {

    private final EventModel model;

    public FastSnapshotProvider(final EventModel model) {
        this.model = model;
    }

    @Override
    public EventModel snapshot() {
        return model;
    }

}
