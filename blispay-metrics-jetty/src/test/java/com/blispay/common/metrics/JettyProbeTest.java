package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.report.BpEventListener;
import com.blispay.common.metrics.report.DefaultBpEventReportingService;
import com.blispay.common.metrics.report.EventFilter;
import com.blispay.common.metrics.util.MetricEvent;
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
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
        assertEquals(1L, meter2xx.getCount().longValue());

        final BpMeter meter4xx = (BpMeter) metricService.getMetric(InstrumentedJettyServer.class, "4xx-responses");
        assertEquals(1L, meter4xx.getCount().longValue());

        final BpTimer endpointTimer = (BpTimer) metricService.getMetric(InstrumentedJettyServer.class, "POST:/user/create/v1");
        assertEquals(1L, endpointTimer.getCount());

        //wiat for gauge to update
        Thread.sleep(5000);
        final BpGauge gauge4xx = (BpGauge) metricService.getMetric(InstrumentedJettyServer.class, "percent-4xx-1m");
        assertEquals(0.5D, (Double) gauge4xx.getValue(), 0.05D);
    }

    @Test
    public void testProducesEndpointTimingEvents() throws Exception {
        final BpMetricService service = new BpMetricService(new DefaultBpEventReportingService());
        final TestableBpEventReporter testReporter = new TestableBpEventReporter();
        service.addEventListener(testReporter);

        final Duration execTime = Duration.ofSeconds(2);
        final JettyProbe probe = new JettyProbe(queuedThreadPool(), simulatedHandler(execTime), service);
        final Server jettyServer = probe.getInstrumentedServer();
        jettyServer.start();

        jettyServer.handle(mockChannel("POST", "/user/create/v1", 200));

        final Queue<MetricEvent> events = testReporter.history();
        assertEquals(1, events.size());

        final MetricEvent evt = events.poll();
        assertThat(evt.print(), CoreMatchers.containsString("eventKey=[[method=[POST],path=[/user/create/v1],statusCode=[200]]]"));
        assertTrue(approximatelyEqual(2000L, Long.valueOf(evt.getValue().toString()), 50L));
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
//        assertEquals(0.0D, utilization.getValue());
        assertEquals(0, jobs.getValue());
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

    private static class TestableBpEventReporter implements BpEventListener {

        private final Set<EventFilter> filters = new HashSet<>();
        private final LinkedList<MetricEvent> events = new LinkedList<>();

        @Override
        public void acceptEvent(final MetricEvent event) {
            events.add(event);
        }

        @Override
        public Collection<EventFilter> getFilters() {
            return filters;
        }

        public void addFilter(final EventFilter filter) {
            this.filters.add(filter);
        }

        public LinkedList<MetricEvent> history() {
            return events;
        }
    }

}
// CHECK_ON: MagicNumber
