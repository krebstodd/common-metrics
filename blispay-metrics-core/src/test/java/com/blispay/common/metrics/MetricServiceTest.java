package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.matchers.BusinessEventMatcher;
import com.blispay.common.metrics.matchers.ResourceCallDataMatcher;
import com.blispay.common.metrics.matchers.ResourceCallMetricMatcher;
import com.blispay.common.metrics.matchers.ResourceUtilizationMetricMatcher;
import com.blispay.common.metrics.metric.BusinessEventRepository;
import com.blispay.common.metrics.metric.DatasourceCallTimer;
import com.blispay.common.metrics.metric.HttpCallTimer;
import com.blispay.common.metrics.metric.InternalResourceCallTimer;
import com.blispay.common.metrics.metric.MqCallTimer;
import com.blispay.common.metrics.metric.ResourceCallTimer;
import com.blispay.common.metrics.metric.ResourceCounter;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
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

    @Test
    public void testGlobalIsSingleton() {

        final MetricService g1 = MetricService.globalInstance();
        final MetricService g2 = MetricService.globalInstance();

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
        final MetricService metricService = new MetricService();

        final ResourceCounter rCounter = metricService.createResourceCounter(MetricGroup.GENERIC, "someResource");

        thrown.expect(IllegalStateException.class);
        rCounter.increment(1L);
    }

    @Test
    public void testCreateResourceUtilizationGauge() {

        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService();
        metricService.addSnapshotReporter(reporter);

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);
        metricService.createResourceUtilizationGauge(MetricGroup.GENERIC, "test-gauge",
                () -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        metricService.start();

        assertThat((ResourceUtilizationMetric) reporter.report().iterator().next(),
                new ResourceUtilizationMetricMatcher(MetricGroup.GENERIC, "test-gauge", MetricType.RESOURCE_UTILIZATION, min, max, curVal.get(), 0.50D));

        curVal.set(75L);

        assertThat((ResourceUtilizationMetric) reporter.report().iterator().next(),
                new ResourceUtilizationMetricMatcher(MetricGroup.GENERIC, "test-gauge", MetricType.RESOURCE_UTILIZATION, min, max, curVal.get(), 0.75D));
    }

    @Test
    public void testCreateResourceUtilizationGaugeDisableSnapshots() {
        final SnapshotReporter reporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService();
        metricService.addSnapshotReporter(reporter);

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);
        metricService.createResourceUtilizationGauge(MetricGroup.GENERIC, "test-gauge",
                () -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max), Boolean.FALSE);

        metricService.start();

        assertFalse(reporter.report().iterator().hasNext());
    }

    @Test
    public void testCreateBusinessEventRepository() {

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService();
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final BusinessEventRepository<PiiBusinessEventData> repo = metricService.createBusinessEventRepository(MetricGroup.APPLICATION, "created");

        repo.save(trackingInfo(), defaultPiiBusinessEventData("user1"));
        repo.save(trackingInfo(), defaultPiiBusinessEventData("user2"));

        assertEquals(2, evtSub.count());

        assertThat((EventMetric<PiiBusinessEventData>) evtSub.poll(),
                new BusinessEventMatcher<>(MetricGroup.APPLICATION, "created", MetricType.EVENT, defaultPiiDataMatcher("user1")));

        assertThat((EventMetric<PiiBusinessEventData>) evtSub.poll(),
                new BusinessEventMatcher<>(MetricGroup.APPLICATION, "created", MetricType.EVENT, defaultPiiDataMatcher("user2")));

    }

    @Test
    public void testCreateHttpResourceCallTimer() throws InterruptedException {

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService();
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final HttpCallTimer timer = metricService.createHttpResourceCallTimer(MetricGroup.GENERIC, "request");

        final ResourceCallTimer.StopWatch sw = timer.start(Direction.INBOUND, HttpResource.fromUrl("/test/url"), HttpAction.POST, trackingInfo());

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.GENERIC, "request", MetricType.RESOURCE_CALL,
                        new ResourceCallDataMatcher(HttpResource.fromUrl("/test/url"), HttpAction.POST, Direction.INBOUND, Status.success(), 1000L)));

    }

    @Test
    public void testCreateDatasourceCallTimer() throws InterruptedException {

        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService();
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final DatasourceCallTimer timer = metricService.createDataSourceCallTimer(MetricGroup.GENERIC, "query");

        final ResourceCallTimer.StopWatch sw = timer.start(new DsResource("dom_account", "applications"), DsAction.INSERT, trackingInfo());

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.GENERIC, "query", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(new DsResource("dom_account", "applications"), DsAction.INSERT, Direction.OUTBOUND, Status.success(), 1000L)));

    }

    @Test
    public void testInternalResourceCallTimer() throws InterruptedException {
        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService();
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final InternalResourceCallTimer timer = metricService.createInternalResourceCallTimer(MetricGroup.GENERIC, "runTest");

        final ResourceCallTimer.StopWatch sw = timer.start(InternalResource.fromClass(getClass()), InternalAction.fromMethodName("testInternalResourceCallTimer"), trackingInfo());

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.GENERIC, "runTest", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(InternalResource.fromClass(getClass()), InternalAction.fromMethodName("testInternalResourceCallTimer"), Direction.OUTBOUND, Status.success(), 1000L)));
    }

    @Test
    public void testMqResourceCallTimer() throws InterruptedException {
        final TestEventSubscriber evtSub = new TestEventSubscriber();

        final MetricService metricService = new MetricService();
        metricService.addEventSubscriber(evtSub);
        metricService.start();

        final MqCallTimer timer = metricService.createMqResourceCallTimer(MetricGroup.GENERIC, "request");

        final ResourceCallTimer.StopWatch sw = timer.start(MqResource.fromQueueName("myqueue"), MqAction.GET, trackingInfo());

        Thread.sleep(1000);

        assertEquals(0, evtSub.count());

        sw.stop(Status.success());

        assertFalse(sw.isRunning());
        assertEquals(1, evtSub.count());
        assertThat(evtSub.poll(), new ResourceCallMetricMatcher(MetricGroup.GENERIC, "request", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(MqResource.fromQueueName("myqueue"), MqAction.GET, Direction.OUTBOUND, Status.success(), 1000L)));
    }

    @Test
    public void testShutsDownDispatcher() {
        final MetricService serv = new MetricService();
        serv.start();

        final ResourceCounter rCounter = serv.createResourceCounter(MetricGroup.GENERIC, "someResource");
        rCounter.increment(1L);

        serv.stop();

        thrown.expect(IllegalStateException.class);
        rCounter.increment(1L);
    }

    @Test
    public void testCustomEventDispatcher() {
        final AtomicBoolean usesCustom = new AtomicBoolean(false);

        final MetricService serv = new MetricService(new EventDispatcher() {
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
            public void start() {

            }

            @Override
            public void stop() {

            }
        });

        serv.start();

        assertFalse(usesCustom.get());
        serv.createResourceCounter(MetricGroup.GENERIC, "someResource").increment(1L);
        assertTrue(usesCustom.get());
    }
}
