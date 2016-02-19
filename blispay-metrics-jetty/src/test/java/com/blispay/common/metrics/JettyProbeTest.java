package com.blispay.common.metrics;

import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.metric.InfrastructureMetricName;
import com.blispay.common.metrics.metric.MetricClass;
import com.blispay.common.metrics.metric.MetricType;
import com.blispay.common.metrics.report.SnapshotReporter;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// CHECK_OFF: MagicNumber
public class JettyProbeTest {

    private static final String appId = "jettyApp";

    @Test
    public void testInstrumentedConnectionFactory() throws Exception {

        final TestMetricEventListener testReporter = new TestMetricEventListener();
        testReporter.addFilter(evt -> evt.getName().toString().equals("metrics.jettyApp.jetty.connection.time"));
        final BpMetricService metricService = new BpMetricService();

        final JettyProbe probe = new JettyProbe(appId, queuedThreadPool(), (channel) -> { }, metricService);
        final ConnectionFactory instrumentedFactory = probe.setAndInstrumentConnectionFactory(new ConnectionFactory() {
            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public Connection newConnection(final Connector connector, final EndPoint endPoint) {
                final Connection connection = mock(HttpConnection.class);
                return connection;
            }
        });

        final Connection connection = instrumentedFactory.newConnection(mock(Connector.class), mock(EndPoint.class));
        connection.onOpen();
        Thread.sleep(300);
        connection.onClose();

    }

    @Test
    public void testInstrumentedServer() throws Exception {
        final TestMetricEventListener testReporter = new TestMetricEventListener();
        testReporter.addFilter(evt -> evt.getName().toString().equals("metrics.jettyApp.jetty.http.request"));

        final BpMetricService metricService = new BpMetricService();
        metricService.addEventListener(testReporter);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final JettyProbe probe = new JettyProbe(appId, queuedThreadPool(), (HttpChannel<?> channel) -> countDownLatch.countDown(), metricService);
        final Server jettyServer = probe.getInstrumentedServer();
        jettyServer.start();

        jettyServer.handle(mockChannel("POST", "/user/create/v1", 200));
        jettyServer.handle(mockChannel("GET", "/user/v1", 404));

        assertEquals(0L, countDownLatch.getCount());

        final MetricEvent event1 = testReporter.history().poll();
        final MetricEvent event2 = testReporter.history().poll();

        final Map<String, String> contextMap = new HashMap<>();
        contextMap.put("path", "/user/create/v1");
        contextMap.put("method", "POST");
        contextMap.put("statusCode", "200");
        assertThat(event1, new MetricEventMatcher(new InfrastructureMetricName("jettyApp", "jetty", "http", "request"), MetricType.PERFORMANCE, MetricClass.apiCall(), contextMap));

        contextMap.put("path", "/user/v1");
        contextMap.put("method", "GET");
        contextMap.put("statusCode", "404");
        assertThat(event2, new MetricEventMatcher(new InfrastructureMetricName("jettyApp", "jetty", "http", "request"), MetricType.PERFORMANCE, MetricClass.apiCall(), contextMap));

    }

    @Test
    public void testThreadPoolMetrics() throws Exception {
        final SnapshotReporter testReporter = new TestSnapshotReporter();

        final BpMetricService metricService = new BpMetricService();
        metricService.addSnapshotReporter(testReporter);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean threadDispatched = new AtomicBoolean(false);

        final QueuedThreadPool tp = queuedThreadPool();
        new JettyProbe(appId, tp, (HttpChannel<?> channel) -> { }, metricService);
        tp.start();

        final Supplier<Boolean> utilizationTest = () ->{
            final MetricEvent event = testReporter.report()
                    .stream()
                    .filter(evt -> evt.getName().toString().equals("metrics.jettyApp.jetty.threadPool.utilization"))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Unable to locate utilization metric"));

            assertThat(event, new MetricEventMatcher("metrics.jettyApp.jetty.threadPool.utilization", "UTIL", "TP", new HashMap<>(), measurementMatcher("0.125", "PCT")));
            return Boolean.TRUE;
        };

        final Supplier<Boolean> countTest = () ->{
            final MetricEvent event = testReporter.report()
                    .stream()
                    .filter(evt -> evt.getName().toString().equals("metrics.jettyApp.jetty.threadPool.threadCount"))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Unable to locate count metric"));

            assertThat(event, new MetricEventMatcher("metrics.jettyApp.jetty.threadPool.threadCount", "UTIL", "TP", new HashMap<>(), measurementMatcher("8", "COUNT")));
            return Boolean.TRUE;
        };

        final Supplier<Boolean> queueSize = () ->{
            final MetricEvent event = testReporter.report()
                    .stream()
                    .filter(evt -> evt.getName().toString().equals("metrics.jettyApp.jetty.threadPool.queueSize"))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Unable to locate queue size metric"));

            assertThat(event, new MetricEventMatcher("metrics.jettyApp.jetty.threadPool.queueSize", "UTIL", "TP", new HashMap<>(), measurementMatcher("0", "COUNT")));
            return Boolean.TRUE;
        };

        tp.execute(() -> {
                utilizationTest.get();
                countTest.get();
                queueSize.get();

                threadDispatched.set(true);
                countDownLatch.countDown();
            });

        countDownLatch.await(1, TimeUnit.SECONDS);
        assertTrue(threadDispatched.get());
    }

    private static <T> MeasurementMatcher measurementMatcher(final T val, final String units) {
        return new MeasurementMatcher("value", val.toString(), "units", units);
    }

    private QueuedThreadPool queuedThreadPool() {
        return new QueuedThreadPool();
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

    private Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
// CHECK_ON: MagicNumber
