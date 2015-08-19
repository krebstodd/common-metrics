package com.blispay.common.metrics;

import com.codahale.metrics.RatioGauge;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.AsyncContextState;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InstrumentedJettyServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentedJettyServer.class);

    private final BpMetricService metricService;

    private final Consumer<HttpChannel<?>> handler;

    private final ConcurrentHashMap<String, BpTimer> endpointTimers = new ConcurrentHashMap<>();

    private BpTimer requests;
    private BpTimer dispatches;
    private BpCounter activeRequests;
    private BpCounter activeDispatches;
    private BpCounter activeSuspended;
    private BpMeter asyncDispatches;
    private BpMeter asyncTimeouts;
    private BpMeter[] responses;
    private BpTimer getRequests;
    private BpTimer postRequests;
    private BpTimer headRequests;
    private BpTimer putRequests;
    private BpTimer deleteRequests;
    private BpTimer optionsRequests;
    private BpTimer traceRequests;
    private BpTimer connectRequests;
    private BpTimer moveRequests;
    private BpTimer otherRequests;
    private AsyncListener listener;

    /**
     * Create a new instrumented jetty service.
     *
     * @param metricService The metric service to use.
     * @param threadPool Thread pool to provide to jetty server.
     * @param channelHandler Channel handler to pass off requests to post-metric collection.
     */
    public InstrumentedJettyServer(final BpMetricService metricService, final ThreadPool threadPool, final Consumer<HttpChannel<?>> channelHandler) {
        super(threadPool);
        this.metricService = metricService;
        this.handler = channelHandler;
    }

    @Override
    public void handle(final HttpChannel<?> channel) throws IOException, ServletException {
        LOG.info("Collecting metrics on jetty https request");
        activeDispatches.increment();

        final BpTimer.Resolver endpointTimer = getEndpointTimer(channel.getRequest()).time();

        final long start;
        final HttpChannelState state = channel.getState();

        if (state.isInitial()) {
            activeRequests.increment();
            start = channel.getRequest().getTimeStamp();
        } else {
            start = Instant.now().toEpochMilli();
            activeSuspended.decrement();
            if (state.getState() == HttpChannelState.State.DISPATCHED) {
                asyncDispatches.mark();
            }
        }

        // Pass the channel down to our application.
        try {
            handler.accept(channel);
        } finally {
            final long now = System.currentTimeMillis();
            final long dispatched = now - start;

            activeDispatches.decrement();
            dispatches.update(dispatched, TimeUnit.MILLISECONDS);

            endpointTimer.done();

            if (state.isSuspended()) {
                if (state.isInitial()) {
                    state.addListener(listener);
                }
                activeSuspended.increment();
            } else if (state.isInitial()) {
                updateResponses(channel.getRequest(), channel.getResponse(), start);
            }
        }
    }

    @Override
    public void handleAsync(final HttpChannel<?> channel) throws IOException, ServletException {
        handler.accept(channel);
    }

    //CHECK_OFF: MagicNumber
    @Override
    public void doStart() throws Exception {
        super.doStart();

        this.requests = metricService.createTimer(InstrumentedJettyServer.class, "requests", "Total number of requests and response time statistics");
        this.dispatches = metricService.createTimer(InstrumentedJettyServer.class, "dispatches", "Total number of disptaches and timing statistics.");
        this.activeRequests = metricService.createCounter(InstrumentedJettyServer.class, "acive-requests", "Total number of in flight requests");
        this.activeDispatches = metricService.createCounter(InstrumentedJettyServer.class, "active-dispatches", "Total number of requests being processed");
        this.activeSuspended = metricService.createCounter(InstrumentedJettyServer.class, "active-suspended", "Total number of suspended requests");
        this.asyncDispatches = metricService.createMeter(InstrumentedJettyServer.class, "async-dispatches", "Rate at which asynchronous requests being processed");
        this.asyncTimeouts = metricService.createMeter(InstrumentedJettyServer.class, "async-timeouts", "Rate at which async requests that have timed out");

        this.responses = new BpMeter[5];
        this.responses[0] = metricService.createMeter(InstrumentedJettyServer.class, "1xx-responses", "Rate at which 100 level responses are created");
        this.responses[1] = metricService.createMeter(InstrumentedJettyServer.class, "2xx-responses", "Rate at which 200 level responses are created");
        this.responses[2] = metricService.createMeter(InstrumentedJettyServer.class, "3xx-responses", "Rate at which 300 level responses are created");
        this.responses[3] = metricService.createMeter(InstrumentedJettyServer.class, "4xx-responses", "Rate at which 400 level responses are created");
        this.responses[4] = metricService.createMeter(InstrumentedJettyServer.class, "5xx-responses", "Rate at which 500 level responses are created");

        this.getRequests = metricService.createTimer(InstrumentedJettyServer.class, "get-requests", "End to end execution time for http GET requests");
        this.postRequests = metricService.createTimer(InstrumentedJettyServer.class, "post-requests", "End to end execution time for http POST requests");
        this.headRequests = metricService.createTimer(InstrumentedJettyServer.class, "head-requests", "End to end execution time for http HEAD requests");
        this.putRequests = metricService.createTimer(InstrumentedJettyServer.class, "put-requests", "End to end execution time for http PUT requests");
        this.deleteRequests = metricService.createTimer(InstrumentedJettyServer.class, "delete-requests", "End to end execution time for http DELETE requests");
        this.optionsRequests = metricService.createTimer(InstrumentedJettyServer.class, "options-requests", "End to end execution time for http OPTIONS requests");
        this.traceRequests = metricService.createTimer(InstrumentedJettyServer.class, "trace-requests", "End to end execution time for http TRACE requests");
        this.connectRequests = metricService.createTimer(InstrumentedJettyServer.class, "connect-requests", "End to end execution time for http CONNECT requests");
        this.moveRequests = metricService.createTimer(InstrumentedJettyServer.class, "move-requests", "End to end execution time for http MOVE requests");
        this.otherRequests = metricService.createTimer(InstrumentedJettyServer.class, "other-requests", "End to end execution time for http OTHER requests");

        metricService.createGauge(InstrumentedJettyServer.class, "percent-4xx-1m", "Percentage of requests that resulted in a 400 level response over past 1 minutes",
                () -> RatioGauge.Ratio.of(responses[3].getOneMinuteRate(), requests.getOneMinuteRate()).getValue());
        metricService.createGauge(InstrumentedJettyServer.class, "percent-4xx-5m", "Percentage of requests that resulted in a 400 level response over past 5 minutes",
                () -> RatioGauge.Ratio.of(responses[3].getFiveMinuteRate(), requests.getFiveMinuteRate()).getValue());
        metricService.createGauge(InstrumentedJettyServer.class, "percent-4xx-15m", "Percentage of requests that resulted in a 400 level response over past 15 minutes",
                () -> RatioGauge.Ratio.of(responses[3].getFifteenMinuteRate(), requests.getFifteenMinuteRate()).getValue());

        metricService.createGauge(InstrumentedJettyServer.class, "percent-5xx-1m", "Percentage of requests that resulted in a 500 level response over past 1 minutes",
                () -> RatioGauge.Ratio.of(responses[4].getOneMinuteRate(), requests.getOneMinuteRate()).getValue());
        metricService.createGauge(InstrumentedJettyServer.class, "percent-5xx-5m", "Percentage of requests that resulted in a 500 level response over past 5 minutes",
                () -> RatioGauge.Ratio.of(responses[4].getFiveMinuteRate(), requests.getFiveMinuteRate()).getValue());
        metricService.createGauge(InstrumentedJettyServer.class, "percent-5xx-15m", "Percentage of requests that resulted in a 500 level response over past 15 minutes",
                () -> RatioGauge.Ratio.of(responses[4].getFifteenMinuteRate(), requests.getFifteenMinuteRate()).getValue());

        this.listener = new WrappedAsyncListener();
    }

    private BpTimer getEndpointTimer(final Request request) {
        return endpointTimers.computeIfAbsent(buildEndpointKey(request), (timerName) ->
                metricService.createTimer(InstrumentedJettyServer.class, timerName, "Timer for " + request.getMethod() + " to uri " + request.getRequestURI()));
    }

    private String buildEndpointKey(final Request request) {
        return request.getMethod() + ":" + request.getRequestURI();
    }

    private BpTimer requestTimer(final String method) {
        final HttpMethod m = HttpMethod.fromString(method);
        if (m == null) {
            return otherRequests;
        } else {
            switch (m) {
                case GET:
                    return getRequests;
                case POST:
                    return postRequests;
                case PUT:
                    return putRequests;
                case HEAD:
                    return headRequests;
                case DELETE:
                    return deleteRequests;
                case OPTIONS:
                    return optionsRequests;
                case TRACE:
                    return traceRequests;
                case CONNECT:
                    return connectRequests;
                case MOVE:
                    return moveRequests;
                default:
                    return otherRequests;
            }
        }
    }

    private void updateResponses(final HttpServletRequest request, final HttpServletResponse response, final long start) {
        final int responseStatus = response.getStatus() / 100;
        if (responseStatus >= 1 && responseStatus <= 5) {
            this.responses[responseStatus - 1].mark();
        }

        this.activeRequests.decrement();
        final long elapsedTime = System.currentTimeMillis() - start;
        this.requests.update(elapsedTime, TimeUnit.MILLISECONDS);
        this.requestTimer(request.getMethod()).update(elapsedTime, TimeUnit.MILLISECONDS);
    }

    private class WrappedAsyncListener implements AsyncListener {
        private long startTime;

        public void onTimeout(final AsyncEvent event) throws IOException {
            InstrumentedJettyServer.this.asyncTimeouts.mark();
        }

        public void onStartAsync(final AsyncEvent event) throws IOException {
            this.startTime = System.currentTimeMillis();
            event.getAsyncContext().addListener(this);
        }

        public void onError(final AsyncEvent event) throws IOException {
        }

        public void onComplete(final AsyncEvent event) throws IOException {
            final AsyncContextState state = (AsyncContextState) event.getAsyncContext();
            final HttpServletRequest request = (HttpServletRequest) state.getRequest();
            final HttpServletResponse response = (HttpServletResponse) state.getResponse();
            InstrumentedJettyServer.this.updateResponses(request, response, this.startTime);
            if (state.getHttpChannelState().getState() != HttpChannelState.State.DISPATCHED) {
                InstrumentedJettyServer.this.activeSuspended.decrement();
            }

        }
    }
    // CHECK_ON: MagicNumber

}
