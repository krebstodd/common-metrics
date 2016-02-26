package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.matchers.BusinessEventMatcher;
import com.blispay.common.metrics.matchers.ResourceCallDataMatcher;
import com.blispay.common.metrics.matchers.ResourceCallMetricMatcher;
import com.blispay.common.metrics.matchers.ResourceUtilizationMetricMatcher;
import com.blispay.common.metrics.metric.DatasourceCallTimer;
import com.blispay.common.metrics.metric.EventRepository;
import com.blispay.common.metrics.metric.HttpCallTimer;
import com.blispay.common.metrics.metric.InternalResourceCallTimer;
import com.blispay.common.metrics.metric.MqCallTimer;
import com.blispay.common.metrics.metric.ResourceCallTimer;
import com.blispay.common.metrics.metric.ResourceCounter;
import com.blispay.common.metrics.metric.ResourceUtilizationGauge;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.business.EventMetric;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.ds.DsAction;
import com.blispay.common.metrics.model.call.ds.DsResource;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.model.call.mq.MqAction;
import com.blispay.common.metrics.model.call.mq.MqResource;
import com.blispay.common.metrics.model.health.HealthCheckData;
import com.blispay.common.metrics.model.health.HealthCheckMetric;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetric;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.SnapshotReporter;
import com.blispay.common.metrics.util.TestEventSubscriber;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MetricServiceTest extends AbstractMetricsTest {

    private static final String application = "testapp";

    @Test
    public void testGlobalIsSingleton() {

        MetricService.setGlobalAppId("someApp");

        final MetricService g1 = MetricService.globalInstance();
        final MetricService g2 = MetricService.globalInstance();

        assertEquals("someApp", g1.getApplicationId());
        assertEquals("someApp", g2.getApplicationId());

        assertFalse(g1.isRunning());
        assertFalse(g2.isRunning());

        g1.start();

        assertTrue(g1.isRunning());
        assertTrue(g2.isRunning());

        g2.stop();

        assertFalse(g1.isRunning());
        assertFalse(g2.isRunning());

        assertTrue(g1.isAutoStartup());
        assertTrue(g2.isAutoStartup());

    }

    @Test
    public void testNotStartedThrowsException() {
        final MetricService metricService = new MetricService(application);

        final ResourceCounter rCounter = metricService.createResourceCounter(MetricGroup.CLIENT, "someResource");

        thrown.expect(IllegalStateException.class);
        rCounter.increment(1L);
    }

    @Test
    public void testCreateResourceUtilizationGauge() {

        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(application);
        metricService.addSnapshotReporter(reporter);

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);
        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "test-gauge",
                () -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        metricService.start();

        assertThat((ResourceUtilizationMetric) reporter.report().getMetrics().iterator().next(),
                new ResourceUtilizationMetricMatcher(MetricGroup.RESOURCE_UTILIZATION_THREADS, "test-gauge", MetricType.RESOURCE_UTILIZATION, min, max, curVal.get(), 0.50D));

        curVal.set(75L);

        assertThat((ResourceUtilizationMetric) reporter.report().getMetrics().iterator().next(),
                new ResourceUtilizationMetricMatcher(MetricGroup.RESOURCE_UTILIZATION_THREADS, "test-gauge", MetricType.RESOURCE_UTILIZATION, min, max, curVal.get(), 0.75D));
    }

    @Test
    public void testCreateResourceUtilizationGaugeDisableSnapshots() {
        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(application);
        metricService.addSnapshotReporter(reporter);

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);
        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "test-gauge",
                () -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max), Boolean.FALSE);

        metricService.start();

        assertFalse(reporter.report().getMetrics().iterator().hasNext());
    }

    @Test
    public void testCreateBusinessEventRepository() {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final EventRepository<PiiBusinessEventData> repo = metricService.createEventRepository(MetricGroup.ACCOUNT_DOMAIN, "created", PiiBusinessEventData.class);

        repo.save(defaultPiiBusinessEventData("user1"));
        repo.save(defaultPiiBusinessEventData("user2"));

        assertEquals(2, evtSub.count());

        assertThat((EventMetric<PiiBusinessEventData>) evtSub.poll(),
                new BusinessEventMatcher<>(MetricGroup.ACCOUNT_DOMAIN, "created", MetricType.EVENT, defaultPiiDataMatcher("user1", trackingInfo)));

        assertThat((EventMetric<PiiBusinessEventData>) evtSub.poll(),
                new BusinessEventMatcher<>(MetricGroup.ACCOUNT_DOMAIN, "created", MetricType.EVENT, defaultPiiDataMatcher("user2", trackingInfo)));

    }

    @Test
    public void testCreateHttpResourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final HttpCallTimer timer = metricService.createHttpResourceCallTimer(MetricGroup.CLIENT_HTTP, "request");

        final ResourceCallTimer.StopWatch sw = timer.start(Direction.INBOUND, HttpResource.fromUrl("/test/url"), HttpAction.POST);

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.CLIENT_HTTP, "request", MetricType.RESOURCE_CALL,
                        new ResourceCallDataMatcher(HttpResource.fromUrl("/test/url"), HttpAction.POST, Direction.INBOUND, Status.success(), 1000L, trackingInfo)));

    }

    @Test
    public void testCreateDatasourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final DatasourceCallTimer timer = metricService.createDataSourceCallTimer(MetricGroup.CLIENT_JDBC, "query");

        final ResourceCallTimer.StopWatch sw = timer.start(new DsResource("dom_account", "applications"), DsAction.INSERT);

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.CLIENT_JDBC, "query", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(new DsResource("dom_account", "applications"), DsAction.INSERT, Direction.OUTBOUND, Status.success(), 1000L, trackingInfo)));

    }

    @Test
    public void testInternalResourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();
        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final InternalResourceCallTimer timer = metricService.createInternalResourceCallTimer(MetricGroup.CLIENT, "runTest");

        final ResourceCallTimer.StopWatch sw = timer.start(InternalResource.fromClass(getClass()), InternalAction.fromMethodName("testInternalResourceCallTimer"));

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.CLIENT, "runTest", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(InternalResource.fromClass(getClass()), InternalAction.fromMethodName("testInternalResourceCallTimer"),
                        Direction.INTERNAL, Status.success(), 1000L, trackingInfo)));
    }

    @Test
    public void testMqResourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final MqCallTimer timer = metricService.createMqResourceCallTimer(MetricGroup.CLIENT_MESSAGE_QUEUE, "request");

        final ResourceCallTimer.StopWatch sw = timer.start(MqResource.fromQueueName("myqueue"), MqAction.GET, "reqQueue", "resQueue", "host", "rType");

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.CLIENT_MESSAGE_QUEUE, "request", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(MqResource.fromQueueName("myqueue"), MqAction.GET, Direction.OUTBOUND, Status.success(), 1000L, trackingInfo)));
    }

    @Test
    public void testCustomEventDispatcher() {
        final AtomicBoolean usesCustom = new AtomicBoolean(false);

        // CHECK_OFF: AnonInnerLength
        final MetricService serv = new MetricService(application, new EventDispatcher() {
            @Override
            public void dispatch(final BaseMetricModel evt) {
                usesCustom.set(true);
            }

            @Override
            public EventEmitter newEventEmitter() {
                return this::dispatch;
            }

            @Override
            public void subscribe(final EventSubscriber listener) {

            }

            @Override
            public void stop(final Runnable runnable) {

            }

            @Override
            public void stop() {

            }

            @Override
            public void start() {

            }

        });
        // CHECK_ON: AnonInnerLength

        serv.start();

        assertFalse(usesCustom.get());
        serv.createResourceCounter(MetricGroup.CLIENT, "someResource").increment(1L);
        assertTrue(usesCustom.get());
    }

    @Test
    public void testRemoveMetric() {

        final MetricService serv = new MetricService("someApp");

        final TestEventSubscriber evtSub = new TestEventSubscriber();
        serv.addEventSubscriber(evtSub);

        final SnapshotReporter reporter = new BasicSnapshotReporter();
        serv.addSnapshotReporter(reporter);

        serv.start();

        // Create a new gauge to test removed metrics no longer report snapshot data.
        final ResourceUtilizationGauge gauge = serv.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "test-gauge",
                () -> new ResourceUtilizationData(1L, 1L, 1L, 1D));

        // Create a new counter to test that removed metrics no longer emit events.
        final ResourceCounter rCounter = serv.createResourceCounter(MetricGroup.CLIENT, "test-counter");

        // Test that this gauge is currently active and reporting snapshots.
        assertEquals(1, reporter.report().getMetrics().size());
        assertEquals("test-gauge", reporter.report().getMetrics().iterator().next().getName());

        // Test that this counter is currently active and emitting events.
        rCounter.increment(1L);
        assertEquals(1, evtSub.count());
        assertEquals("test-counter", evtSub.poll().getName());

        // Remove the metrics, the return value should be present (the service was able to remove the metric.)
        assertTrue(serv.removeMetricRepository(gauge).isPresent());
        assertTrue(serv.removeMetricRepository(rCounter).isPresent());

        // Test that the counter does not emit events and that the gauge does not report.
        assertEquals(0, reporter.report().getMetrics().size());
        rCounter.increment(1L);
        assertEquals(0, evtSub.count());

    }

    @Test
    public void testCachesEventRepositoriesByDatTypeAndGroupAndName() {

        final MetricService serv = new MetricService("someApp");
        final TestEventSubscriber evtSub = new TestEventSubscriber();
        serv.addEventSubscriber(evtSub);

        serv.start();

        // The service should notice that r1 and r2 have the same group, name, and event data type and should cache them.
        final EventRepository r1 = serv.createEventRepository(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA", PiiBusinessEventData.class);
        final EventRepository r2 = serv.createEventRepository(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA", PiiBusinessEventData.class);
        final EventRepository r3 = serv.createEventRepository(MetricGroup.INTERNAL_METHOD_CALL, "repositoryB", PiiBusinessEventData.class);

        assertTrue(r1 == r2);
        assertFalse(r1 == r3);

        // Assert that event's are not double published.
        r1.save(defaultPiiBusinessEventData());
        assertEquals(1, evtSub.count());
        evtSub.poll();

        // Remove r1 and assert that r2 doesn't publish events any longer.
        serv.removeMetricRepository(r1);
        r2.save(defaultPiiBusinessEventData());
        assertEquals(0, evtSub.count());

    }

    @Test
    public void testCachesTimersByDataGroupAndName() {

        final MetricService serv = new MetricService("someApp");
        final TestEventSubscriber evtSub = new TestEventSubscriber();
        serv.addEventSubscriber(evtSub);

        serv.start();

        // R2 should get the cached version of R1
        final ResourceCounter r1 = serv.createResourceCounter(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final ResourceCounter r2 = serv.createResourceCounter(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final ResourceCounter r3 = serv.createResourceCounter(MetricGroup.ACCOUNT_DOMAIN, "repositoryA");

        assertTrue(r1 == r2);
        assertFalse(r1 == r3);

        // Assert that event's are not double published.
        r1.increment(1L);
        assertEquals(1, evtSub.count());
        evtSub.poll();

        // Remove r1 and assert that r2 doesn't publish events any longer.
        serv.removeMetricRepository(r1);
        r2.increment(1L);
        assertEquals(0, evtSub.count());

    }

    @Test
    public void testCachesCountersByDataGroupAndName() {

        final MetricService serv = new MetricService("someApp");
        final TestEventSubscriber evtSub = new TestEventSubscriber();
        serv.addEventSubscriber(evtSub);

        serv.start();

        // R2 should get the cached version of R1
        final ResourceCounter r1 = serv.createResourceCounter(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final ResourceCounter r2 = serv.createResourceCounter(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final ResourceCounter r3 = serv.createResourceCounter(MetricGroup.ACCOUNT_DOMAIN, "repositoryA");

        assertTrue(r1 == r2);
        assertFalse(r1 == r3);

        // Assert that event's are not double published.
        r1.increment(1L);
        assertEquals(1, evtSub.count());
        evtSub.poll();

        // Remove r1 and assert that r2 doesn't publish events any longer.
        serv.removeMetricRepository(r1);
        r2.increment(1L);
        assertEquals(0, evtSub.count());

    }

    @Test
    public void testCachesHttpTimersByDataGroupAndName() {

        final MetricService serv = new MetricService("someApp");
        final TestEventSubscriber evtSub = new TestEventSubscriber();
        serv.addEventSubscriber(evtSub);

        serv.start();

        // R2 should get the cached version of R1
        final HttpCallTimer r1 = serv.createHttpResourceCallTimer(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final HttpCallTimer r2 = serv.createHttpResourceCallTimer(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final HttpCallTimer r3 = serv.createHttpResourceCallTimer(MetricGroup.ACCOUNT_DOMAIN, "repositoryA");

        // Test that timers of different types aren't confused.
        final ResourceCallTimer r4 = serv.createMqResourceCallTimer(MetricGroup.ACCOUNT_DOMAIN, "repositoryA");

        assertTrue(r1 == r2);
        assertFalse(r1 == r3);
        assertFalse(r3 == r4);

        // Assert that event's are not double published.
        r1.start(Direction.INBOUND, HttpResource.fromUrl("/"), HttpAction.POST).stop(Status.success());
        assertEquals(1, evtSub.count());
        evtSub.poll();

        // Remove r1 and assert that r2 doesn't publish events any longer.
        serv.removeMetricRepository(r1);
        r2.start(Direction.INBOUND, HttpResource.fromUrl("/"), HttpAction.POST).stop(Status.success());
        assertEquals(0, evtSub.count());

    }

    @Test
    public void testCachesMqTimersByDataGroupAndName() {

        final MetricService serv = new MetricService("someApp");
        final TestEventSubscriber evtSub = new TestEventSubscriber();
        serv.addEventSubscriber(evtSub);

        serv.start();

        // R2 should get the cached version of R1
        final MqCallTimer r1 = serv.createMqResourceCallTimer(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final MqCallTimer r2 = serv.createMqResourceCallTimer(MetricGroup.INTERNAL_METHOD_CALL, "repositoryA");
        final MqCallTimer r3 = serv.createMqResourceCallTimer(MetricGroup.ACCOUNT_DOMAIN, "repositoryA");

        assertTrue(r1 == r2);
        assertFalse(r1 == r3);

        // Assert that event's are not double published.
        r1.start(MqResource.fromQueueName("queue"), MqAction.GET, "", "", "", "").stop(Status.success());
        assertEquals(1, evtSub.count());
        evtSub.poll();

        // Remove r1 and assert that r2 doesn't publish events any longer.
        serv.removeMetricRepository(r1);
        r2.start(MqResource.fromQueueName("queue"), MqAction.GET, "", "", "", "").stop(Status.success());
        assertEquals(0, evtSub.count());

    }

    @Test
    public void testHealthMonitor() {

        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(application);
        metricService.addSnapshotReporter(reporter);

        final AtomicBoolean isHealthy = new AtomicBoolean(Boolean.TRUE);
        metricService.createHealthMonitor(MetricGroup.HEALTH, "my-resource", () -> new HealthCheckData(isHealthy.get(), "Some message"));

        final HealthCheckMetric health = (HealthCheckMetric) reporter.report().getMetrics().iterator().next();
        assertEquals("my-resource", health.getName());
        assertEquals(MetricGroup.HEALTH, health.getGroup());
        assertTrue(health.eventData().isHealthy());

        isHealthy.set(Boolean.FALSE);

        final HealthCheckMetric health2 = (HealthCheckMetric) reporter.report().getMetrics().iterator().next();
        assertEquals("my-resource", health2.getName());
        assertEquals(MetricGroup.HEALTH, health2.getGroup());
        assertFalse(health2.eventData().isHealthy());

    }

}
