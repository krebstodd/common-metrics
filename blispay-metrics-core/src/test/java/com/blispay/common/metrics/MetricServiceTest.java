package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.TrackingInfoMatcher;
import com.blispay.common.metrics.matchers.TransactionDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.model.call.ds.DsAction;
import com.blispay.common.metrics.model.call.ds.DsResource;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.model.call.mq.MqAction;
import com.blispay.common.metrics.model.call.mq.MqResource;
import com.blispay.common.metrics.model.counter.ResourceCountData;
import com.blispay.common.metrics.model.health.HealthCheckData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.SnapshotReporter;
import com.blispay.common.metrics.util.TestEventSubscriber;
import org.hamcrest.Matchers;
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

        g2.stop();

        assertFalse(g1.isRunning());
        assertFalse(g2.isRunning());

        g1.start();

        assertTrue(g1.isRunning());
        assertTrue(g2.isRunning());

        assertTrue(g1.isAutoStartup());
        assertTrue(g2.isAutoStartup());

    }

    @Test
    public void testNotStartedThrowsException() {
        final MetricService metricService = new MetricService(application);

        thrown.expect(IllegalStateException.class);

        metricService.eventRepository(PiiBusinessEventData.class)
                .ofType(EventType.BUSINESS_EVT)
                .inGroup(EventGroup.CLIENT)
                .withName("someResource")
                .build()
                .save(defaultPiiBusinessEventData());
    }

    @Test
    public void testCreateResourceUtilizationGauge() {

        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(application);
        metricService.addSnapshotReporter(reporter);

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);

        metricService.utilizationGauge()
                .inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .withName("test-gauge")
                .register(() -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        metricService.start();

        curVal.set(75L);
    }

    @Test
    public void testCreateBusinessEventRepository() {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        metricService.eventRepository(PiiBusinessEventData.class)
                .ofType(EventType.BUSINESS_EVT)
                .inGroup(EventGroup.ACCOUNT_DOMAIN)
                .withName("created")
                .build()
                .save(defaultPiiBusinessEventData("user1"));

        metricService.eventRepository(PiiBusinessEventData.class)
                .ofType(EventType.BUSINESS_EVT)
                .inGroup(EventGroup.ACCOUNT_DOMAIN)
                .withName("created")
                .build()
                .save(defaultPiiBusinessEventData("user2"));

        assertEquals(2, evtSub.count());

        assertThat((EventModel<PiiBusinessEventData>) evtSub.poll(),
                new EventMatcher<>(application, EventGroup.ACCOUNT_DOMAIN, "created", EventType.BUSINESS_EVT, defaultPiiDataMatcher("user1", trackingInfo)));

        assertThat((EventModel<PiiBusinessEventData>) evtSub.poll(),
                new EventMatcher<>(application, EventGroup.ACCOUNT_DOMAIN, "created", EventType.BUSINESS_EVT, defaultPiiDataMatcher("user2", trackingInfo)));

    }

    @Test
    public void testCreateHttpResourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final Transaction tx = metricService.transactionFactory()
                .inGroup(EventGroup.CLIENT_HTTP)
                .withName("request")
                .inDirection(Direction.OUTBOUND)
                .withAction(HttpAction.POST)
                .onResource(HttpResource.fromUrl("/test/url"))
                .build()
                .create();

        tx.start();

        Thread.sleep(1000);
        assertEquals(0, evtSub.count());

        tx.success();

        assertFalse(tx.isRunning());
        assertEquals(1, evtSub.count());
        assertThat((EventModel<TransactionData>) evtSub.poll(),
                new EventMatcher<>(application, EventGroup.CLIENT_HTTP, "request", EventType.RESOURCE_CALL,
                        new TransactionDataMatcher(HttpResource.fromUrl("/test/url"), HttpAction.POST, Direction.OUTBOUND, Status.success(), 1000L, new TrackingInfoMatcher(trackingInfo))));

    }

    @Test
    public void testCreateDatasourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final Transaction tx = metricService.transactionFactory()
                .inGroup(EventGroup.CLIENT_JDBC)
                .withName("query")
                .inDirection(Direction.OUTBOUND)
                .withAction(DsAction.INSERT)
                .onResource(DsResource.fromSchemaTable("dom_account", "applications"))
                .build()
                .create();

        tx.start();

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        tx.success();

        assertFalse(tx.isRunning());
        assertEquals(1, evtSub.count());

        assertThat((EventModel<TransactionData>) evtSub.poll(),
                new EventMatcher<>(application, EventGroup.CLIENT_JDBC, "query", EventType.RESOURCE_CALL,
                        new TransactionDataMatcher(new DsResource("dom_account", "applications"), DsAction.INSERT, Direction.OUTBOUND, Status.success(), 1000L, Matchers.notNullValue())));

    }

    @Test
    public void testInternalResourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();
        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final Transaction tx = metricService.transactionFactory()
                .inGroup(EventGroup.CLIENT_JDBC)
                .withName("query")
                .inDirection(Direction.OUTBOUND)
                .withAction(InternalAction.fromMethodName("doSomething"))
                .onResource(InternalResource.fromClass(getClass()))
                .build()
                .create();

        tx.start();

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        tx.success();
        assertFalse(tx.isRunning());
        assertEquals(1, evtSub.count());

        assertThat((EventModel<TransactionData>) evtSub.poll(),
                new EventMatcher<>(application, EventGroup.CLIENT_JDBC, "query", EventType.RESOURCE_CALL,
                        new TransactionDataMatcher(InternalResource.fromClass(getClass()),  InternalAction.fromMethodName("doSomething"),
                                Direction.OUTBOUND, Status.success(), 1000L, new TrackingInfoMatcher(trackingInfo))));
    }

    @Test
    public void testMqResourceCallTimer() throws InterruptedException {
        final TrackingInfo trackingInfo = trackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final Transaction tx = metricService.transactionFactory()
                .inGroup(EventGroup.CLIENT_MQ_REQ)
                .withName("request")
                .inDirection(Direction.OUTBOUND)
                .withAction(MqAction.GET)
                .onResource(MqResource.fromQueueName("myqueue"))
                .build()
                .create();

        tx.start();

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        tx.success();
        assertFalse(tx.isRunning());
        assertEquals(1, evtSub.count());

        assertThat((EventModel<TransactionData>) evtSub.poll(),
                new EventMatcher<>(application, EventGroup.CLIENT_MQ_REQ, "request", EventType.RESOURCE_CALL,
                        new TransactionDataMatcher(MqResource.fromQueueName("myqueue"), MqAction.GET,
                                Direction.OUTBOUND, Status.success(), 1000L, new TrackingInfoMatcher(trackingInfo))));

    }

    @Test
    public void testCustomEventDispatcher() {
        final AtomicBoolean usesCustom = new AtomicBoolean(false);

        // CHECK_OFF: AnonInnerLength
        final MetricService serv = new MetricService(application, new EventDispatcher() {
            @Override
            public void dispatch(final EventModel evt) {
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
            public void unSubscribe(final EventSubscriber listener) {

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

        serv.eventRepository(ResourceCountData.class)
                .ofType(EventType.RESOURCE_COUNT)
                .inGroup(EventGroup.CLIENT)
                .withName("someResource")
                .build()
                .save(new ResourceCountData(1D));

        assertTrue(usesCustom.get());
    }

    @Test
    public void testHealthMonitor() {

        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(application);
        metricService.addSnapshotReporter(reporter);

        final AtomicBoolean isHealthy = new AtomicBoolean(Boolean.TRUE);
        metricService.healthMonitor()
                .inGroup(EventGroup.HEALTH)
                .withName("my-resource")
                .withSupplier(() -> new HealthCheckData(isHealthy.get(), "Some message"))
                .register();

        final EventModel health = reporter.report().getMetrics().iterator().next();
        assertEquals("my-resource", health.getName());
        assertEquals(EventGroup.HEALTH, health.getGroup());
        assertTrue(health.eventData() instanceof HealthCheckData);
        assertTrue(((HealthCheckData) health.eventData()).isHealthy());

        isHealthy.set(Boolean.FALSE);

        final EventModel health2 = reporter.report().getMetrics().iterator().next();
        assertEquals("my-resource", health2.getName());
        assertEquals(EventGroup.HEALTH, health2.getGroup());
        assertTrue(health.eventData() instanceof HealthCheckData);
        assertFalse(((HealthCheckData) health2.eventData()).isHealthy());

    }

}
