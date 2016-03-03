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

// CHECK_OFF: MagicNumber
public class JettyProbeTest {

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

        final EventModel<TransactionData> event1 = testReporter.poll();
        final EventModel<TransactionData> event2 = testReporter.poll();

        assertThat(event1, new EventMatcher(metricService.getApplicationId(), EventGroup.SERVER_HTTP, "http-response", EventType.RESOURCE_CALL,
                new TransactionDataMatcher(HttpResource.fromUrl("/user/create/v1"), HttpAction.POST, Direction.INBOUND, Status.fromValue(200), 1000L)));

        assertThat(event2, new EventMatcher(metricService.getApplicationId(), EventGroup.SERVER_HTTP, "http-response", EventType.RESOURCE_CALL,
                new TransactionDataMatcher(HttpResource.fromUrl("/user/v1"), HttpAction.GET, Direction.INBOUND, Status.fromValue(404), 1000L)));
    }

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

        tp.execute(() -> {
                final Set<EventModel> snapshot = testReporter.report().getMetrics();
                assertEquals(1, snapshot.size());


                assertThat((EventModel<ResourceUtilizationData>) snapshot.iterator().next(),
                        new EventMatcher<>(metricService.getApplicationId(), EventGroup.RESOURCE_UTILIZATION_THREADS, "jetty-thread-pool", EventType.RESOURCE_UTILIZATION,
                                new ResourceUtilizationDataMatcher(0L, 8L, 1L, 0.125D)));

                threadDispatched.set(true);
                countDownLatch.countDown();
            });

        countDownLatch.await(1, TimeUnit.SECONDS);
        assertTrue(threadDispatched.get());
    }

    private static Consumer<HttpChannel<?>> simulatedHandler(final Duration simulatedExecutionTime) {
        return (channel) -> {
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
// CHECK_ON: MagicNumber
