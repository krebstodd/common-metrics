package com.blispay.common.metrics.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class BasicSnapshotReporter.
 */
public class BasicSnapshotReporter extends SnapshotReporter {

    private static Logger LOG = LoggerFactory.getLogger(BasicSnapshotReporter.class);

    /**
     * Constructs BasicSnapshotReporter.
     */
    public BasicSnapshotReporter() {
        this(new SingleThreadedCollectionStrategy());
    }

    /**
     * Constructs BasicSnapshotReporter.
     *
     * @param snapshotCollector snapshotCollector.
     */
    public BasicSnapshotReporter(final SnapshotCollectionStrategy snapshotCollector) {
        super(snapshotCollector);
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public Boolean isRunning() {
        return Boolean.TRUE;
    }

    @Override
    public Logger logger() {
        return LOG;
    }

}
