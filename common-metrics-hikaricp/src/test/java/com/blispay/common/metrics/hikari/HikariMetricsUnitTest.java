package com.blispay.common.metrics.hikari;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.TransactionDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.ConcurrentCollectionStrategy;
import com.blispay.common.metrics.report.Slf4jEventReporter;
import com.blispay.common.metrics.report.Slf4jSnapshotReporter;
import com.blispay.common.metrics.report.Snapshot;
import com.blispay.common.metrics.report.SnapshotScheduler;
import com.zaxxer.hikari.metrics.MetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Unit tests for {@link HikariMetricsTrackerFactory}.
 */
public class HikariMetricsUnitTest {

    private MetricService metricService;
    private HikariMetricsTrackerFactory factory;

    /**
     * Init.
     */
    @Before
    public void init() {
        this.metricService = new MetricService(UUID.randomUUID().toString());
        this.metricService.addEventSubscriber(new Slf4jEventReporter(LoggerFactory.getLogger(getClass())));
        this.metricService.addSnapshotReporter(new Slf4jSnapshotReporter(LoggerFactory.getLogger(getClass()),
                SnapshotScheduler.scheduleFixedDelay(Duration.ofMillis(750)),
                new ConcurrentCollectionStrategy(1, Duration.ofSeconds(10))));
        this.metricService.start();
        this.factory = new HikariMetricsTrackerFactory(this.metricService);
    }

    /**
     * Destroy.
     */
    @After
    public void destroy() {
        if (this.metricService != null) {
            this.metricService.stop();
        }
    }

    @Test
    public void testPublishesConnectionAcquiredTxMetric() throws InterruptedException {
        // Add an event subscriber to capture published metrics.
        final TestEventSubscriber subscriber = new TestEventSubscriber();
        this.metricService.addEventSubscriber(subscriber);

        // Create a metrics tracker from the factory.
        final String poolName = UUID.randomUUID().toString();
        final PoolStats mockPoolStats = Mockito.mock(PoolStats.class);
        final long acquireTimeNanos = RandomUtils.nextLong();
        final MetricsTracker tracker = factory.create(poolName, mockPoolStats);

        // Tell the tracker to record time.
        tracker.recordConnectionAcquiredNanos(acquireTimeNanos);

        // Test that the correct metric event was published.
        Assert.assertEquals("Verify one metric published", 1, subscriber.count());
        final EventModel<TransactionData, HikariMetricsTracker.ConnectionAcquisitionInfo> published = subscriber.poll();
        Assert.assertThat(published, EventMatcher.<TransactionData, HikariMetricsTracker.ConnectionAcquisitionInfo>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.CLIENT_JDBC_CONN_POOL)
                .setName("hikaricp-acquire-conn")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(
                        Resource.withName(poolName),
                        Action.withName("acquire"),
                        Direction.INTERNAL,
                        Status.success(),
                        Duration.ofNanos(acquireTimeNanos).toMillis(),
                        0L))
                // The user data section of the metric should include an item for each element in the stack trace who's declaring
                // class package starts with com.blispay excluding those that are actually tied to the HikariMetricsTracker itself.
                .setUserDataMatcher(createConnectionAcquisitionMatcher(createStackTraceMatcher("testPublishesConnectionAcquiredTxMetric")))
                .build());

        Thread.sleep(10000);

    }

    @Test
    public void testPublishesConnectionUsageTxMetric() {
        // Add an event subscriber to capture published metrics.
        final TestEventSubscriber subscriber = new TestEventSubscriber();
        this.metricService.addEventSubscriber(subscriber);

        // Create a metrics tracker from the factory.
        final String poolName = UUID.randomUUID().toString();
        final PoolStats mockPoolStats = Mockito.mock(PoolStats.class);
        final long usageTimeMillis = RandomUtils.nextLong();
        final MetricsTracker tracker = factory.create(poolName, mockPoolStats);

        // Tell the tracker to record time.
        tracker.recordConnectionUsageMillis(usageTimeMillis);

        // Test that the correct metric event was published.
        Assert.assertEquals("Verify one metric published", 1, subscriber.count());
        final EventModel<TransactionData, HikariMetricsTracker.ConnectionUsageInfo> published = subscriber.poll();
        Assert.assertThat(published, EventMatcher.<TransactionData, HikariMetricsTracker.ConnectionUsageInfo>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.CLIENT_JDBC_CONN_POOL)
                .setName("hikaricp-use-conn")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(
                        Resource.withName(poolName),
                        Action.withName("usage"),
                        Direction.INTERNAL,
                        Status.success(),
                        usageTimeMillis,
                        0L))
                // The user data section of the metric should include an item for each element in the stack trace who's declaring
                // class package starts with com.blispay excluding those that are actually tied to the HikariMetricsTracker itself.
                .setUserDataMatcher(createConnectionUsageMatcher(createStackTraceMatcher("testPublishesConnectionUsageTxMetric")))
                .build());
    }

    @Test
    public void testPublishesConnectionTimeoutEvent() {
        // Add an event subscriber to capture published metrics.
        final TestEventSubscriber subscriber = new TestEventSubscriber();
        this.metricService.addEventSubscriber(subscriber);

        // Create a metrics tracker from the factory.
        final String poolName = UUID.randomUUID().toString();
        final PoolStats mockPoolStats = Mockito.mock(PoolStats.class);
        final MetricsTracker tracker = factory.create(poolName, mockPoolStats);

        // Tell the tracker to record connection timeout.
        tracker.recordConnectionTimeout();

        // Test that the correct metric event was published.
        Assert.assertEquals("Verify one metric published", 1, subscriber.count());
        final EventModel<Void, HikariMetricsTracker.ConnectionTimeoutInfo> published = subscriber.poll();
        Assert.assertThat(published, EventMatcher.<Void, HikariMetricsTracker.ConnectionTimeoutInfo>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.CLIENT_JDBC_CONN_POOL)
                .setName("hikaricp-pool-timeout")
                .setType(EventType.EVENT)
                // The user data section of the metric should include an item for each element in the stack trace who's declaring
                // class package starts with com.blispay excluding those that are actually tied to the HikariMetricsTracker itself.
                .setUserDataMatcher(createConnectionTimeoutMatcher(createStackTraceMatcher("testPublishesConnectionTimeoutEvent")))
                .build());
    }

    @Test
    public void testPublishesPoolStatsGauge() {
        // Create a metrics tracker from the factory.
        final String poolName = UUID.randomUUID().toString();
        final PoolStats mockPoolStats = Mockito.mock(PoolStats.class);

        final int activeConnections = RandomUtils.nextInt();
        final int idleConnections = RandomUtils.nextInt();
        final int pendingConnections = RandomUtils.nextInt();
        final int totalConnections = RandomUtils.nextInt();
        Mockito.when(mockPoolStats.getActiveConnections()).thenReturn(activeConnections);
        Mockito.when(mockPoolStats.getIdleConnections()).thenReturn(idleConnections);
        Mockito.when(mockPoolStats.getPendingThreads()).thenReturn(pendingConnections);
        Mockito.when(mockPoolStats.getTotalConnections()).thenReturn(totalConnections);

        factory.create(poolName, mockPoolStats);

        final BasicSnapshotReporter reporter = new BasicSnapshotReporter();
        this.metricService.addSnapshotReporter(reporter);
        final Snapshot snapshot = reporter.report();
        final Set<EventModel> published = snapshot.getMetrics();

        Assert.assertEquals("Verify one gauge metric published", 1, published.size());

        final EventModel<Void, HikariMetricsTracker.HikariPoolStats> poolStatsSnapshot = published.iterator().next();
        Assert.assertThat(poolStatsSnapshot, EventMatcher.<Void, HikariMetricsTracker.HikariPoolStats>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.RESOURCE_UTILIZATION_JDBC_CONN_POOL)
                .setName("hikaricp-pool-usage")
                .setType(EventType.EVENT)
                .setUserDataMatcher(HikariPoolStatsMatcher.fromPoolStats(mockPoolStats))
                .build());
    }

    @Test
    public void testRemovesGaugeOnClose() {
        // Create a metrics tracker from the factory.
        final String poolName = UUID.randomUUID().toString();
        final PoolStats mockPoolStats = Mockito.mock(PoolStats.class);

        final MetricsTracker tracker = factory.create(poolName, mockPoolStats);

        final BasicSnapshotReporter reporter = new BasicSnapshotReporter();
        this.metricService.addSnapshotReporter(reporter);

        // Close the tracker and ensure that it stops showing up in snapshots.
        tracker.close();

        Assert.assertTrue("Verify closed trackers top reporting.", reporter.report().getMetrics().isEmpty());
    }

    private Matcher<Iterable<? extends String>> createStackTraceMatcher(final String testMethodName) {
        return Matchers.contains(
                Matchers.startsWith("com.blispay.common.metrics.hikari.HikariMetricsUnitTest." + testMethodName + "(HikariMetricsUnitTest.java:")
        );
    }

    private ConnectionTimeoutMatcher createConnectionTimeoutMatcher(final Matcher<Iterable<? extends String>> stackTraceMatcher) {
        return new ConnectionTimeoutMatcher(createStackTraceInfoMatcher(stackTraceMatcher));
    }

    private ConnectionUsageMatcher createConnectionUsageMatcher(final Matcher<Iterable<? extends String>> stackTraceMatcher) {
        return new ConnectionUsageMatcher(createStackTraceInfoMatcher(stackTraceMatcher));
    }

    private ConnectionAcquisitionMatcher createConnectionAcquisitionMatcher(final Matcher<Iterable<? extends String>> stackTraceMatcher) {
        return new ConnectionAcquisitionMatcher(createStackTraceInfoMatcher(stackTraceMatcher));
    }
    
    private StackTraceInfoMatcher createStackTraceInfoMatcher(final Matcher<Iterable<? extends String>> stackTraceMatcher) {
        return new StackTraceInfoMatcher(stackTraceMatcher);
    }

    private static final class StackTraceInfoMatcher extends TypeSafeMatcher<HikariMetricsTracker.StackTraceInfo> {

        private final Matcher<Iterable<? extends String>> items;

        private StackTraceInfoMatcher(final Matcher<Iterable<? extends String>> items) {
            this.items = items;
        }

        @Override
        protected boolean matchesSafely(final HikariMetricsTracker.StackTraceInfo stackTraceInfo) {
            return items.matches(stackTraceInfo.getItems());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("items=[").appendValue(this.items).appendText("]");
        }
    }

    private static final class ConnectionTimeoutMatcher extends TypeSafeMatcher<HikariMetricsTracker.ConnectionTimeoutInfo> {

        private final StackTraceInfoMatcher stackTraceInfoMatcher;

        private ConnectionTimeoutMatcher(final StackTraceInfoMatcher stackTraceInfoMatcher) {
            this.stackTraceInfoMatcher = stackTraceInfoMatcher;
        }

        @Override
        protected boolean matchesSafely(final HikariMetricsTracker.ConnectionTimeoutInfo connectionTimeoutInfo) {
            return this.stackTraceInfoMatcher.matches(connectionTimeoutInfo.getStackTrace());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("stackTraceInfo=[").appendDescriptionOf(stackTraceInfoMatcher).appendText("]");
        }
    }

    private static final class ConnectionUsageMatcher extends TypeSafeMatcher<HikariMetricsTracker.ConnectionUsageInfo> {

        private final StackTraceInfoMatcher stackTraceInfoMatcher;

        private ConnectionUsageMatcher(final StackTraceInfoMatcher stackTraceInfoMatcher) {
            this.stackTraceInfoMatcher = stackTraceInfoMatcher;
        }

        @Override
        protected boolean matchesSafely(final HikariMetricsTracker.ConnectionUsageInfo connectionUsageInfo) {
            return this.stackTraceInfoMatcher.matches(connectionUsageInfo.getStackTrace());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("stackTraceInfo=[").appendDescriptionOf(stackTraceInfoMatcher).appendText("]");
        }
    }

    private static final class ConnectionAcquisitionMatcher extends TypeSafeMatcher<HikariMetricsTracker.ConnectionAcquisitionInfo> {

        private final StackTraceInfoMatcher stackTraceInfoMatcher;

        private ConnectionAcquisitionMatcher(final StackTraceInfoMatcher stackTraceInfoMatcher) {
            this.stackTraceInfoMatcher = stackTraceInfoMatcher;
        }

        @Override
        protected boolean matchesSafely(final HikariMetricsTracker.ConnectionAcquisitionInfo connectionAcquisitionInfo) {
            return this.stackTraceInfoMatcher.matches(connectionAcquisitionInfo.getStackTrace());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("stackTraceInfo=[").appendDescriptionOf(stackTraceInfoMatcher).appendText("]");
        }
    }

    private static final class HikariPoolStatsMatcher extends TypeSafeMatcher<HikariMetricsTracker.HikariPoolStats> {

        private final Matcher<Integer> total;
        private final Matcher<Integer> active;
        private final Matcher<Integer> idle;
        private final Matcher<Integer> pending;


        private HikariPoolStatsMatcher(final int total,
                                       final int active,
                                       final int idle,
                                       final int pending) {

            this.total = Matchers.equalTo(total);
            this.active = Matchers.equalTo(active);
            this.idle = Matchers.equalTo(idle);
            this.pending = Matchers.equalTo(pending);
        }

        @Override
        protected boolean matchesSafely(final HikariMetricsTracker.HikariPoolStats hikariPoolStats) {
            return this.total.matches(hikariPoolStats.getTotal())
                    && this.active.matches(hikariPoolStats.getActive())
                    && this.idle.matches(hikariPoolStats.getIdle())
                    && this.pending.matches(hikariPoolStats.getPendingThreads());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("total=[").appendDescriptionOf(total).appendText("],");
            description.appendText("active=[").appendDescriptionOf(active).appendText("],");
            description.appendText("idle=[").appendDescriptionOf(idle).appendText("],");
            description.appendText("pending=[").appendDescriptionOf(pending).appendText("]");
        }

        public static Matcher<HikariMetricsTracker.HikariPoolStats> fromPoolStats(final PoolStats mockPoolStats) {
            return new HikariPoolStatsMatcher(mockPoolStats.getTotalConnections(),
                    mockPoolStats.getActiveConnections(),
                    mockPoolStats.getIdleConnections(),
                    mockPoolStats.getPendingThreads());
        }
    }
}
