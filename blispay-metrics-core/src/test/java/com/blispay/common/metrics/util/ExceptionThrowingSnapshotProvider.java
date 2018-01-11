package com.blispay.common.metrics.util;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.report.SnapshotProvider;

/**
 * Snapshot provider that throws an exception every time {@link SnapshotProvider#snapshot()} is called. For test
 * purposes only.
 */
public class ExceptionThrowingSnapshotProvider implements SnapshotProvider {

    private final RuntimeException toThrow;

    /**
     * Create a new SN provider.
     * @param toThrow Exception that should be thrown on snapshot.
     */
    public ExceptionThrowingSnapshotProvider(final RuntimeException toThrow) {
        this.toThrow = toThrow;
    }

    @Override
    public EventModel snapshot() {
        throw toThrow;
    }

    @Override
    public String id() {
        return "exceptionThrowingSnapshotProvider";
    }

    @Override
    public String description() {
        return "Test snapshot provider that throws an when called.";
    }
}
