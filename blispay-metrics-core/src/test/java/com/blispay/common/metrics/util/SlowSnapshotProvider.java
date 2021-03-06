package com.blispay.common.metrics.util;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.report.SnapshotProvider;

import java.time.Duration;

/**
 * Class SlowSnapshotProvider.
 */
public class SlowSnapshotProvider implements SnapshotProvider {

    private final Duration waitTime;
    private final EventModel model;

    /**
     * Slow snapshot provider.
     * @param waitTime Amount of time to wait before returning value.
     * @param model Model to return.
     */
    public SlowSnapshotProvider(final Duration waitTime, final EventModel model) {

        this.waitTime = waitTime;
        this.model = model;
    }

    @Override
    public EventModel snapshot() {

        try {
            Thread.sleep(waitTime.toMillis());
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return model;

    }

    @Override
    public String id() {
        return "slowSnapshotProvider";
    }

    @Override
    public String description() {
        return "A test snapshot provider that simulates slowly executing snapshots.";
    }

}
