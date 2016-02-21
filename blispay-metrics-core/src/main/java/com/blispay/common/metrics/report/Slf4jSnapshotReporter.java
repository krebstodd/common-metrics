package com.blispay.common.metrics.report;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.MetricSerializer;
import com.blispay.common.metrics.model.BaseMetricModel;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Slf4jSnapshotReporter extends ScheduledSnapshotReporter {

    private final MetricSerializer serializer;
    private final Logger logger;
    private Supplier<Set<SnapshotProvider>> snapshotProviderSupplier;

    /**
     * Create a new scheduled reporter. Subclass must implement a report method that will be called periodically.
     *
     * @param logger the logger to use.
     * @param period The period between calls to report method.
     * @param unit   The time unit of the period argument.
     */
    public Slf4jSnapshotReporter(final Logger logger, final Integer period, final TimeUnit unit) {
        this(new JsonMetricSerializer(), logger, period, unit);
    }

    public Slf4jSnapshotReporter(final MetricSerializer serializer, final Logger logger, final Integer period, final TimeUnit timeUnit) {
        super(period, timeUnit);

        this.serializer = serializer;
        this.logger = logger;
        this.snapshotProviderSupplier = HashSet::new;
    }

    @Override
    public Set<BaseMetricModel> report() {
        final Set<BaseMetricModel> snapshot = snapshotProviderSupplier.get().stream().map(SnapshotProvider::snapshot).collect(Collectors.toSet());
        snapshot.forEach(ss -> logger.info(serializer.serialize(ss)));
        return snapshot;
    }

    @Override
    public void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers) {
        this.snapshotProviderSupplier = providers;
    }
}
