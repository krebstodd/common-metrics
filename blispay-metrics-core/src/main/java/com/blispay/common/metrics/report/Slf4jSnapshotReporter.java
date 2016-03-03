package com.blispay.common.metrics.report;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.EventSerializer;
import com.blispay.common.metrics.model.EventModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Slf4jSnapshotReporter extends ScheduledSnapshotReporter {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jSnapshotReporter.class);

    private final EventSerializer serializer;
    private final Logger logger;
    private Supplier<Set<SnapshotProvider>> snapshotProviderSupplier;

    public Slf4jSnapshotReporter(final Logger logger, final Integer period, final TimeUnit timeUnit) {
        this(new JsonMetricSerializer(), logger, period, timeUnit);
    }

    /**
     * Create a new scheduled reporter. Subclass must implement a report method that will be called periodically.
     *
     * @param serializer metric serializer to user.
     * @param logger the logger to use.
     * @param period The period between calls to report method.
     * @param timeUnit The time unit of the period argument.
     */
    public Slf4jSnapshotReporter(final EventSerializer serializer, final Logger logger, final Integer period, final TimeUnit timeUnit) {
        super(period, timeUnit);

        this.serializer = serializer;
        this.logger = logger;
        this.snapshotProviderSupplier = HashSet::new;
    }

    @Override
    public Snapshot report() {

        LOG.info("Starting snapshot report...");

        final Set<EventModel> snapshot = snapshotProviderSupplier.get().stream().map(SnapshotProvider::snapshot).collect(Collectors.toSet());
        snapshot.forEach(ss -> logger.info(serializer.serialize(ss)));

        LOG.info("Snapshot report complete.");

        return new Snapshot(snapshot);
    }

    @Override
    public void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers) {
        this.snapshotProviderSupplier = providers;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
