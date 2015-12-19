package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpTimer;
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
import org.junit.After;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// CHECK_OFF: MagicNumber
public class JettyProbeTest {

    private static final BpMetricService metricService = BpMetricService.globalInstance();

    @After
    public void clearMetrics() {
        metricService.clear();
    }

    @Test
    public void testInstrumentedConnectionFactory() throws Exception {

        final JettyProbe probe = new JettyProbe(queuedThreadPool(), (channel) -> { }, metricService);
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
        Thread.sleep(1000);
        connection.onClose();
    }

    @Test
    public void testInstrumentedServer() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final JettyProbe probe = new JettyProbe(queuedThreadPool(), (HttpChannel<?> channel) -> countDownLatch.countDown(), metricService);
        final Server jettyServer = probe.getInstrumentedServer();
        jettyServer.start();

        jettyServer.handle(mockChannel("POST", "/user/create/v1", 200));
        jettyServer.handle(mockChannel("GET", "/user/v1", 404));

        assertEquals(0L, countDownLatch.getCount());

        final BpTimer requestsTimer = (BpTimer) metricService.getMetric(InstrumentedJettyServer.class, "requests");
        assertEquals(2L, requestsTimer.getCount());

        final BpMeter meter2xx = (BpMeter) metricService.getMetric(InstrumentedJettyServer.class, "2xx-responses");
        assertEquals(1L, meter2xx.getCount());

        final BpMeter meter4xx = (BpMeter) metricService.getMetric(InstrumentedJettyServer.class, "4xx-responses");
        assertEquals(1L, meter4xx.getCount());

        final BpTimer endpointTimer = (BpTimer) metricService.getMetric(InstrumentedJettyServer.class, "POST:/user/create/v1");
        assertEquals(1L, endpointTimer.getCount());

        //wiat for gauge to update
        Thread.sleep(5000);
        final BpGauge gauge4xx = (BpGauge) metricService.getMetric(InstrumentedJettyServer.class, "percent-4xx-1m");
        assertEquals(0.5D, (Double) gauge4xx.getValue(), 0.05D);
    }

    @Test
    public void testThreadPoolMetrics() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final QueuedThreadPool tp = queuedThreadPool();
        new JettyProbe(tp, (HttpChannel<?> channel) -> { }, metricService);
        tp.start();

        final BpGauge poolSize = (BpGauge) metricService.getMetric(QueuedThreadPool.class, "size");
        final BpGauge utilization = (BpGauge) metricService.getMetric(QueuedThreadPool.class, "utilization");
        final BpGauge jobs = (BpGauge) metricService.getMetric(QueuedThreadPool.class, "jobs");

        tp.execute(() -> {
                assertEquals(0.125D, utilization.getValue()); // There are 8 threads and we're using one.
                countDownLatch.countDown();
            });

        countDownLatch.await(1, TimeUnit.SECONDS);

        assertEquals(tp.getThreads(), poolSize.getValue());
        assertEquals(0.0D, utilization.getValue());
        assertEquals(0, jobs.getValue());
    }

    private QueuedThreadPool queuedThreadPool() {
        return new QueuedThreadPool();
    }

    private HttpChannel mockChannel(final String method, final String uri, final Integer responseCode) {
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

    private Boolean approximatelyEqual(final Double expected, final Double actual, final Double acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
// CHECK_ON: MagicNumber
