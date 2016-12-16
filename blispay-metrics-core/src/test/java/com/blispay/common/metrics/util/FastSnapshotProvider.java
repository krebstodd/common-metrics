package com.blispay.common.metrics.util;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.report.SnapshotProvider;

/**
 * Class FastSnapshotProvider.
 */
public class FastSnapshotProvider implements SnapshotProvider {

    private final EventModel model;

    /**
     * Constructs FastSnapshotProvider.
     *
     * @param model model.
     */
    public FastSnapshotProvider(final EventModel model) {
        this.model = model;
    }

    @Override
    public EventModel snapshot() {
        return model;
    }

}
