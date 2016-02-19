package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.metric.InfrastructureMetricName;
import com.blispay.common.metrics.metric.MetricContext;
import com.blispay.common.metrics.metric.MetricClass;
import com.blispay.common.metrics.util.StopWatch;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.function.Consumer;

public class InstrumentedJettyServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentedJettyServer.class);

    private final String applicationId;
    private final BpMetricService metricService;

    private final Consumer<HttpChannel<?>> handler;

    private BpTimer requestTimer;

    /**
     * Create a new instrumented jetty service.
     *
     * @param applicationId The applications unique identifier.
     * @param metricService The metric service to use.
     * @param threadPool Thread pool to provide to jetty server.
     * @param channelHandler Channel handler to pass off requests to post-metric collection.
     */
    public InstrumentedJettyServer(final String applicationId,
                                   final BpMetricService metricService,
                                   final ThreadPool threadPool,
                                   final Consumer<HttpChannel<?>> channelHandler) {

        super(threadPool);
        this.applicationId = applicationId;
        this.metricService = metricService;
        this.handler = channelHandler;
    }

    @Override
    public void handle(final HttpChannel<?> channel) throws IOException, ServletException {
        LOG.info("Collecting metrics on jetty https request");

        final StopWatch requestStopwatch = requestTimer.time();

        // Pass the channel down to our application.
        try {
            handler.accept(channel);
        } finally {
            requestStopwatch.stop(buildRequestContext(channel.getRequest(), channel.getResponse()));
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
        this.requestTimer = metricService.createTimer(new InfrastructureMetricName(applicationId, "jetty", "http", "request"), MetricClass.apiCall());
    }

    private MetricContext buildRequestContext(final Request request, final Response response) {
        return HttpRequestMetricContextFactory.createContext(request.getMethod(), response.getStatus(), request.getRequestURI());
    }

    // CHECK_ON: MagicNumber

}
