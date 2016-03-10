package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.ResourceUtilizationDataMatcher;
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
import com.blispay.common.metrics.model.status.StatusData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.SnapshotReporter;
import com.blispay.common.metrics.transaction.Transaction;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.TestEventSubscriber;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MetricServiceTest extends AbstractMetricsTest {

    private static final String application = "testapp";

    @Before
    public void clearTrackingInfo() {
        LocalMetricContext.clear();
    }

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

        metricService.eventFactory(PiiBusinessEventData.class)
                .inGroup(EventGroup.CLIENT_HTTP_DDS)
                .withName("someResource")
                .build()
                .save(defaultPiiBusinessEventData());
    }

    @Test
    public void testCreateResourceUtilizationGauge() {

        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(application);
        metricService.start();
        metricService.addSnapshotReporter(reporter);

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);

        metricService.utilizationGauge()
                .inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .withName("test-gauge")
                .register(() -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        final EventMatcher<ResourceUtilizationData, Void> m1 = EventMatcher.<ResourceUtilizationData, Void>builder()
                .setApplication(application)
                .setGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .setName("test-gauge")
                .setType(EventType.RESOURCE_UTILIZATION)
                .setDataMatcher(new ResourceUtilizationDataMatcher(Matchers.equalTo(min), Matchers.equalTo(max), Matchers.equalTo(curVal.get()), Matchers.equalTo((double) curVal.get() / max)))
                .build();

        assertThat((EventModel<ResourceUtilizationData, Void>) reporter.report().getMetrics().iterator().next(), m1);
    }

    @Test
    public void testCreateBusinessEventRepository() {

        final TrackingInfo trackingInfo = createAndSetThreadLocalTrackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        metricService.eventFactory(PiiBusinessEventData.class)
                .inGroup(EventGroup.ACCOUNT_DOMAIN)
                .withName("created")
                .build()
                .save(defaultPiiBusinessEventData("user1"));

        metricService.eventFactory(PiiBusinessEventData.class)
                .inGroup(EventGroup.ACCOUNT_DOMAIN)
                .withName("created")
                .build()
                .save(defaultPiiBusinessEventData("user2"));

        assertEquals(2, evtSub.count());

        final EventMatcher<Void, PiiBusinessEventData> m1 = EventMatcher.<Void, PiiBusinessEventData>builder()
                .setApplication(application)
                .setGroup(EventGroup.ACCOUNT_DOMAIN)
                .setName("created")
                .setType(EventType.EVENT)
                .setUserDataMatcher(defaultPiiDataMatcher("user1"))
                .setTrackingInfoMatcher(new TrackingInfoMatcher(trackingInfo))
                .build();

        final EventMatcher<Void, PiiBusinessEventData> m2 = EventMatcher.<Void, PiiBusinessEventData>builder()
                .setApplication(application)
                .setGroup(EventGroup.ACCOUNT_DOMAIN)
                .setName("created")
                .setType(EventType.EVENT)
                .setUserDataMatcher(defaultPiiDataMatcher("user2"))
                .setTrackingInfoMatcher(new TrackingInfoMatcher(trackingInfo))
                .build();

        assertThat((EventModel<Void, PiiBusinessEventData>) evtSub.poll(), m1);
        assertThat((EventModel<Void, PiiBusinessEventData>) evtSub.poll(), m2);

    }

    @Test
    public void testCreateHttpResourceCallTimer() throws InterruptedException {

        final TrackingInfo trackingInfo = createAndSetThreadLocalTrackingInfo();

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService(application);
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final Transaction tx = metricService.transactionFactory()
                .inGroup(EventGroup.SERVER_HTTP)
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

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(application)
                .setGroup(EventGroup.SERVER_HTTP)
                .setName("request")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(HttpResource.fromUrl("/test/url"), HttpAction.POST, Direction.OUTBOUND, Status.success(), 1000L))
                .setTrackingInfoMatcher(new TrackingInfoMatcher(trackingInfo))
                .build();

        assertThat((EventModel<TransactionData, Void>) evtSub.poll(), matcher);

    }

    @Test
    public void testCreateDatasourceCallTimer() throws InterruptedException {
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

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(application)
                .setGroup(EventGroup.CLIENT_JDBC)
                .setName("query")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(new DsResource("dom_account", "applications"), DsAction.INSERT, Direction.OUTBOUND, Status.success(), 1000L))
                .build();

        assertThat((EventModel<TransactionData, Void>) evtSub.poll(), matcher);

    }

    @Test
    public void testInternalResourceCallTimer() throws InterruptedException {
        createAndSetThreadLocalTrackingInfo();
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

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(application)
                .setGroup(EventGroup.CLIENT_JDBC)
                .setName("query")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(InternalResource.fromClass(getClass()), InternalAction.fromMethodName("doSomething"),
                        Direction.OUTBOUND, Status.success(), 1000L))
                .setTrackingInfoMatcher(Matchers.notNullValue())
                .build();

        assertThat((EventModel<TransactionData, Void>) evtSub.poll(), matcher);

    }

    @Test
    public void testMqResourceCallTimer() throws InterruptedException {
        createAndSetThreadLocalTrackingInfo();
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

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(application)
                .setGroup(EventGroup.CLIENT_MQ_REQ)
                .setName("request")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(MqResource.fromQueueName("myqueue"), MqAction.GET,
                        Direction.OUTBOUND, Status.success(), 1000L))
                .setTrackingInfoMatcher(Matchers.notNullValue())
                .build();

        assertThat((EventModel<TransactionData, Void>) evtSub.poll(), matcher);

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

        serv.eventFactory(ResourceCountData.class)
                .inGroup(EventGroup.SERVER_HTTP)
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
        metricService.stateMonitor()
                .inGroup(EventGroup.ACCOUNT_DOMAIN_HEALTH)
                .withName("my-resource")
                .withSupplier(() -> new StatusData(isHealthy.get(), "Some message"))
                .register();

        final EventModel health = reporter.report().getMetrics().iterator().next();
        assertEquals("my-resource", health.getHeader().getName());
        assertEquals(EventGroup.ACCOUNT_DOMAIN_HEALTH, health.getHeader().getGroup());
        assertTrue(health.getData() instanceof StatusData);
        assertTrue(((StatusData) health.getData()).getStatusValue());

        isHealthy.set(Boolean.FALSE);

        final EventModel health2 = reporter.report().getMetrics().iterator().next();
        assertEquals("my-resource", health.getHeader().getName());
        assertEquals(EventGroup.ACCOUNT_DOMAIN_HEALTH, health.getHeader().getGroup());
        assertTrue(health.getData() instanceof StatusData);
        assertFalse(((StatusData) health2.getData()).getStatusValue());

    }

}
