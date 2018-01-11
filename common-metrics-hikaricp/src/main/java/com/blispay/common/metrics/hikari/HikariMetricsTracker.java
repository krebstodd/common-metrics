package com.blispay.common.metrics.hikari;

import com.blispay.common.metrics.EventFactory;
import com.blispay.common.metrics.Gauge;
import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.transaction.ManualTransaction;
import com.blispay.common.metrics.transaction.TransactionFactory;
import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.metrics.MetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrapper class for the {@link MetricsTracker} hikari interface. Publishes Hikari CP metrics to the {@link MetricService}.
 */
class HikariMetricsTracker extends MetricsTracker {

    private static final Logger LOG = LoggerFactory.getLogger(HikariMetricsTracker.class);

    /**
     * Indicates the maximum number of com.blispay stack trace items to print in the metric user data. The most recent
     * n items will be printed.
     */
    private static final int MAX_STACK_TRACE_LENGTH = 3;

    /**
     * Name of the Hikari CP pool provided by the {@link MetricsTrackerFactory}.
     */
    private final String poolName;
    /**
     * Pool stats object provided by the {@link MetricsTrackerFactory}.
     */
    private final PoolStats poolStats;
    /**
     * Service metrics should be published to.
     */
    private final MetricService metricService;
    /**
     * Transaction factory against which time to acquire connection transactional metrics should be build. A metric
     * is published each time a thread acquire a connection indicating how long the thread waited for a connection
     * to become available.
     */
    private final TransactionFactory connectionAcquiredTxFactory;
    /**
     * Transaction factory against which time of connection utilization transactional metrics should be build. A metric
     * is published each time a thread releases a connection indicating how long the thread used the connection before
     * releasing it back to the pool.
     */
    private final TransactionFactory connectionUsageTxFactory;
    /**
     * Gauge capable of producing usage statistics for the Hikari CP. See {@link HikariPoolStats} for more info.
     */
    private final Gauge<HikariPoolStats> poolUsageGauge;
    /**
     * Event factory against which connection timeout events are built. An event
     */
    private final EventFactory<ConnectionTimeoutInfo> connectionTimeoutEventFactory;

    HikariMetricsTracker(final String poolName,
                         final PoolStats poolStats,
                         final MetricService metricService) {

        Validate.notBlank(poolName, "Connection pool name required.");
        Validate.notNull(poolStats, "Pool stats required.");
        Validate.notNull(metricService, "Metric service required.");

        this.poolName = poolName;
        this.poolStats = poolStats;
        this.metricService = metricService;

        final Resource trackedResource = Resource.withName(poolName);

        this.connectionAcquiredTxFactory = metricService.transactionFactory()
                .withName("hikaricp-acquire-conn")
                .onResource(trackedResource)
                .inGroup(EventGroup.CLIENT_JDBC_CONN_POOL)
                .inDirection(Direction.INTERNAL)
                .withAction(Action.withName("acquire"))
                .build();

        this.connectionUsageTxFactory = metricService.transactionFactory()
                .withName("hikaricp-use-conn")
                .onResource(trackedResource)
                .inGroup(EventGroup.CLIENT_JDBC_CONN_POOL)
                .inDirection(Direction.INTERNAL)
                .withAction(Action.withName("usage"))
                .build();

        this.poolUsageGauge = metricService.gauge(HikariPoolStats.class)
                .withName("hikaricp-pool-usage")
                .inGroup(EventGroup.RESOURCE_UTILIZATION_JDBC_CONN_POOL)
                .onResource(trackedResource)
                .register(this::getPoolStatsData);

        this.connectionTimeoutEventFactory = metricService.eventFactory(ConnectionTimeoutInfo.class)
                .withName("hikaricp-pool-timeout")
                .inGroup(EventGroup.CLIENT_JDBC_CONN_POOL)
                .onResource(trackedResource)
                .build();
    }

    @Override
    public void recordConnectionAcquiredNanos(final long elapsedAcquiredNanos) {
        publish(this.connectionAcquiredTxFactory.createManual(),
                new ConnectionAcquisitionInfo(createBlispayOnlyStackTraceInfo()),
                Duration.ofNanos(elapsedAcquiredNanos));
    }

    @Override
    public void recordConnectionUsageMillis(final long elapsedBorrowedMillis) {
        publish(this.connectionUsageTxFactory.createManual(),
                new ConnectionUsageInfo(createBlispayOnlyStackTraceInfo()),
                Duration.ofMillis(elapsedBorrowedMillis));
    }

    @Override
    public void recordConnectionTimeout() {
        this.connectionTimeoutEventFactory.save(new ConnectionTimeoutInfo(createBlispayOnlyStackTraceInfo()));
    }

    @Override
    public void close() {
        this.metricService.removeSnapshotProvider(this.poolUsageGauge);
    }

    private HikariPoolStats getPoolStatsData() {
        return HikariPoolStats.create(this.poolStats);
    }

    private void publish(final ManualTransaction transaction,
                         final Object userInfo,
                         final Duration period) {
        try {
            transaction.userData(userInfo);
            transaction.success(period);
        // CHECK_OFF: IllegalCatch
        } catch (Throwable throwable) {
            LOG.error("Caught exception attempting to stop hikari transaction for pool [{}]", this.poolName, throwable);
        }
        // CHECK_ON: IllegalCatch
    }

    /**
     * Instantiate a new builder.
     * @return New builder.
     */
    static Builder create() {
        return new Builder();
    }

    /**
     * Returns a {@link StackTraceInfo} object containing stack trace items (class, method, line) for every
     * {@link StackTraceElement} on the current threads stack trace who's class is in a com.blispay package. Excludes
     * any stack trace elements from teh current class (HikariMetricsTracker). By filtering non-blispay stack frames,
     * developers can narrow in on exactly part of the application is causing tension on the connection pool.
     *
     * @return Stack trace info.
     */
    private static StackTraceInfo createBlispayOnlyStackTraceInfo() {
        final List<StackTraceElement> stackTrace = Stream.of(Thread.currentThread().getStackTrace())
                .filter(element -> !element.getClassName().equals(HikariMetricsTracker.class.getName()))
                .filter(element -> element.getClassName().startsWith("com.blispay"))
                .collect(Collectors.toList());

        final int stackTraceLength = Math.min(stackTrace.size(), MAX_STACK_TRACE_LENGTH);
        return new StackTraceInfo(stackTrace.subList(0, stackTraceLength));
    }

    /**
     * Builder impl.
     */
    static final class Builder {

        private String poolName;
        private PoolStats poolStats;
        private MetricService metricService = MetricService.globalInstance();

        Builder forPoolName(final String poolName) {
            this.poolName = poolName;
            return this;
        }

        Builder withPoolStats(final PoolStats poolStats) {
            this.poolStats = poolStats;
            return this;
        }

        Builder publishTo(final MetricService metricService) {
            this.metricService = metricService;
            return this;
        }

        HikariMetricsTracker build() {
            return new HikariMetricsTracker(poolName, poolStats, metricService);
        }

    }

    /**
     * Pojo holding stack trace information published in the user data section of Hikari CP metrics. Allows developers
     * to get some context as to what was happening prior to the metric being published. Note that this may not
     * include a full stack trace as users may want to filter for a max size or only certain packages. See
     * {@link HikariMetricsTracker#createBlispayOnlyStackTraceInfo()} for an example.
     */
    static final class StackTraceInfo {

        private final List<String> items;

        private StackTraceInfo(final List<StackTraceElement> elements) {
            this.items = ImmutableList.copyOf(elements.stream()
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList()));
        }

        public List<String> getItems() {
            return this.items;
        }

    }

    /**
     * Pojo holding connection timeout info published in the user data section of connection timeout event metrics.
     */
    static final class ConnectionTimeoutInfo {

        private final StackTraceInfo stackTrace;

        private ConnectionTimeoutInfo(final StackTraceInfo stackTrace) {
            this.stackTrace = stackTrace;
        }

        public StackTraceInfo getStackTrace() {
            return stackTrace;
        }

    }

    /**
     * Pojo holding stack trace information published in the user data section of connection acquired metrics.
     */
    static final class ConnectionAcquisitionInfo {

        private final StackTraceInfo stackTrace;

        private ConnectionAcquisitionInfo(final StackTraceInfo stackTrace) {
            this.stackTrace = stackTrace;
        }

        public StackTraceInfo getStackTrace() {
            return stackTrace;
        }

    }

    /**
     * Pojo holding stack trace information published in the user data section of connection usage metrics.
     */
    static final class ConnectionUsageInfo {

        private final StackTraceInfo stackTrace;

        private ConnectionUsageInfo(final StackTraceInfo stackTrace) {
            this.stackTrace = stackTrace;
        }

        public StackTraceInfo getStackTrace() {
            return stackTrace;
        }

    }

    /**
     * Pojo holding an immutable snapshot of the data from a {@link PoolStats} object for use in metrics.
     */
    static final class HikariPoolStats {

        private final int total;
        private final int idle;
        private final int active;
        private final int pendingThreads;

        HikariPoolStats(final int total,
                        final int idle,
                        final int active,
                        final int pendingThreads) {
            this.total = total;
            this.idle = idle;
            this.active = active;
            this.pendingThreads = pendingThreads;
        }

        public int getTotal() {
            return total;
        }

        public int getIdle() {
            return idle;
        }

        public int getActive() {
            return active;
        }

        public int getPendingThreads() {
            return pendingThreads;
        }

        /**
         * Create a new snapshot.
         *
         * @param poolStats Underlying pool stats object.
         * @return Immutable point in time snapshot of pool stats.
         */
        public static HikariPoolStats create(final PoolStats poolStats) {
            return new HikariPoolStats(
                    poolStats.getTotalConnections(),
                    poolStats.getIdleConnections(),
                    poolStats.getActiveConnections(),
                    poolStats.getPendingThreads()
            );
        }
    }
}
