package com.blispay.common.metrics.report;

import com.blispay.common.metrics.data.EventSerializer;
import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.model.EventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jSnapshotReporter extends ScheduledSnapshotReporter {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jSnapshotReporter.class);

    private final EventSerializer serializer;
    private final Logger mericLogger;

    public Slf4jSnapshotReporter(final Logger mericLogger,
                                 final SnapshotScheduler scheduler,
                                 final SnapshotCollectionStrategy snapshotCollectionStrategy) {

        this(new JsonMetricSerializer(), mericLogger, scheduler, snapshotCollectionStrategy);
    }

    /**
     * Create a new scheduled reporter. Subclass must implement a report method that will be called periodically.
     *
     * @param serializer metric serializer to user.
     * @param mericLogger the logger to use for dumping metrics logs.
     * @param scheduler Schedules snapshots.
     * @param snapshotCollectionStrategy Collection strategy.
     */
    public Slf4jSnapshotReporter(final EventSerializer serializer,
                                 final Logger mericLogger,
                                 final SnapshotScheduler scheduler,
                                 final SnapshotCollectionStrategy snapshotCollectionStrategy) {

        super(scheduler, snapshotCollectionStrategy);

        this.serializer = serializer;
        this.mericLogger = mericLogger;
    }

    @Override
    public Logger logger() {
        return LOG;
    }

    @Override
    protected void handleScheduledSnapshot(final Snapshot snapshot) {
        snapshot.getMetrics().forEach(this::logMetricEvent);
    }

    private void logMetricEvent(final EventModel event) {
        mericLogger.info(serializer.serialize(event));
    }

}

