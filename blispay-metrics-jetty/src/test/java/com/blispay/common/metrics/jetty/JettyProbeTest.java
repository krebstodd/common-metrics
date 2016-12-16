package com.blispay.common.metrics.jetty;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.ResourceUtilizationDataMatcher;
import com.blispay.common.metrics.matchers.TransactionDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.SnapshotReporter;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//CHECK_OFF: MagicNumber

/**
 * Class JettyProbeTest.
 */
public class JettyProbeTest {

    /**
     * Method testInstrumentedServer.
     *
     * @throws Exception Exception.
     */
    @Test
    public void testInstrumentedServer() throws Exception {

        final TestEventSubscriber testReporter = new TestEventSubscriber();

        final MetricService metricService = new MetricService(MetricTestUtil.randomAppId());
        metricService.addEventSubscriber(testReporter);
        metricService.start();

        final int maxThreads = 8;
        final JettyProbe probe = new JettyProbe(queuedThreadPool(maxThreads), simulatedHandler(Duration.ofSeconds(1)), metricService);
        final Server jettyServer = probe.getInstrumentedServer();
        jettyServer.start();

        jettyServer.handle(mockChannel("POST", "/user/create/v1", 200));
        jettyServer.handle(mockChannel("GET", "/user/v1", 404));

        final EventModel<TransactionData, Void> event1 = testReporter.poll();
        final EventModel<TransactionData, Void> event2 = testReporter.poll();

        final EventMatcher<TransactionData, Void> m1 = EventMatcher.<TransactionData, Void>builder()
                                                                   .setApplication(metricService.getApplicationId())
                                                                   .setGroup(EventGroup.SERVER_HTTP)
                                                                   .setName("http-response")
                                                                   .setType(EventType.TRANSACTION)
                                                                   .setDataMatcher(new TransactionDataMatcher(HttpResource.fromUrl("/user/create/v1"),
                                                                                                              HttpAction.POST,
                                                                                                              Direction.INBOUND,
                                                                                                              Status.fromValue(200),
                                                                                                              1000L))
                                                                   .build();

        final EventMatcher<TransactionData, Void> m2 = EventMatcher.<TransactionData, Void>builder()
                                                                   .setApplication(metricService.getApplicationId())
                                                                   .setGroup(EventGroup.SERVER_HTTP)
                                                                   .setName("http-response")
                                                                   .setType(EventType.TRANSACTION)
                                                                   .setDataMatcher(new TransactionDataMatcher(HttpResource.fromUrl("/user/v1"),
                                                                                                              HttpAction.GET,
                                                                                                              Direction.INBOUND,
                                                                                                              Status.fromValue(404),
                                                                                                              1000L))
                                                                   .build();

        assertThat(event1, m1);

        assertThat(event2, m2);
    }

    /**
     * Method testThreadPoolMetrics.
     *
     * @throws Exception Exception.
     */
    @Test
    @Ignore
    public void testThreadPoolMetrics() throws Exception {
        final SnapshotReporter testReporter = new BasicSnapshotReporter();

        final MetricService metricService = new MetricService(MetricTestUtil.randomAppId());
        metricService.addSnapshotReporter(testReporter);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean threadDispatched = new AtomicBoolean(false);

        final QueuedThreadPool tp = queuedThreadPool(8);
        new JettyProbe(tp, (HttpChannel<?> channel) -> { }, metricService);
        tp.start();

        tp.execute(
            () -> {
                final Set<EventModel> snapshot = testReporter.report().getMetrics();
                assertEquals(1, snapshot.size());

                final EventMatcher<ResourceUtilizationData, Void> m1 = EventMatcher.<ResourceUtilizationData, Void>builder()
                                                                                   .setApplication(metricService.getApplicationId())
                                                                                   .setGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                                                                                   .setName("jetty-thread-pool")
                                                                                   .setType(EventType.RESOURCE_UTILIZATION)
                                                                                   .setDataMatcher(new ResourceUtilizationDataMatcher(Matchers.equalTo(0L),
                                                                                                                                      Matchers.equalTo(8L),
                                                                                                                                      Matchers.equalTo(1L),
                                                                                                                                      Matchers.equalTo(0.125D)))
                                                                                   .build();

                assertThat((EventModel<ResourceUtilizationData, Void>) snapshot.iterator().next(), m1);

                threadDispatched.set(true);
                countDownLatch.countDown();
            });

        countDownLatch.await(1, TimeUnit.SECONDS);
        assertTrue(threadDispatched.get());
    }

    private static Consumer<HttpChannel<?>> simulatedHandler(final Duration simulatedExecutionTime) {
        return channel -> {
            try {
                Thread.sleep(simulatedExecutionTime.toMillis());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    private QueuedThreadPool queuedThreadPool(final Integer maxThreads) {
        return new QueuedThreadPool(maxThreads);
    }

    private HttpChannel mockChannel(final String method, final String uri, final Integer responseCode) throws InterruptedException {
        final HttpChannel<?> channel = mock(HttpChannel.class);
        final Request request = mock(Request.class);
        final Response response = mock(Response.class);
        final HttpChannelState state = mock(HttpChannelState.class);

        when(state.isInitial()).thenReturn(true);
        when(channel.getState()).thenReturn(state);

        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getTimeStamp()).thenReturn(Instant.now().toEpochMilli());
        when(channel.getRequest()).thenReturn(request);

        when(response.getStatus()).thenReturn(responseCode);
        when(channel.getResponse()).thenReturn(response);

        return channel;
    }

}

//CHECK_ON: MagicNumber
