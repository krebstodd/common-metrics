package com.blispay.common.metrics.jetty;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.ResourceCallDataMatcher;
import com.blispay.common.metrics.matchers.ResourceCallMetricMatcher;
import com.blispay.common.metrics.matchers.ResourceUtilizationMetricMatcher;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetric;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetric;
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

        final HttpResourceCallMetric event1 = (HttpResourceCallMetric) testReporter.poll();
        final HttpResourceCallMetric event2 = (HttpResourceCallMetric) testReporter.poll();

        assertThat(event1, new ResourceCallMetricMatcher(MetricGroup.SERVER_HTTP, "http-response", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(HttpResource.fromUrl("/user/create/v1"), HttpAction.POST, Direction.INBOUND, Status.fromValue(200), 1000L)));

        assertThat(event2, new ResourceCallMetricMatcher(MetricGroup.SERVER_HTTP, "http-response", MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(HttpResource.fromUrl("/user/v1"), HttpAction.GET, Direction.INBOUND, Status.fromValue(404), 1000L)));
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
                final Set<BaseMetricModel> snapshot = testReporter.report().getMetrics();
                assertEquals(1, snapshot.size());

                assertThat((ResourceUtilizationMetric) snapshot.iterator().next(),
                        new ResourceUtilizationMetricMatcher(MetricGroup.RESOURCE_UTILIZATION_THREADS, "jetty-thread-pool", MetricType.RESOURCE_UTILIZATION, 0L, 8L, 1L, 0.125D));

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
