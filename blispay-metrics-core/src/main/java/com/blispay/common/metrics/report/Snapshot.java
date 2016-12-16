package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

import java.util.Collections;
import java.util.Set;

/**
 * Class Snapshot.
 */
public class Snapshot {

    private final Set<EventModel> metrics;

    /**
     * Constructs Snapshot.
     *
     * @param metrics metrics.
     */
    public Snapshot(final Set<EventModel> metrics) {
        this.metrics = Collections.unmodifiableSet(metrics);
    }

    /**
     * Method getMetrics.
     *
     * @return return value.
     */
    public Set<EventModel> getMetrics() {
        return metrics;
    }

}
