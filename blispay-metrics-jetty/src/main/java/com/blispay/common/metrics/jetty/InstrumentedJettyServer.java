package com.blispay.common.metrics.jetty;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.metric.HttpCallTimer;
import com.blispay.common.metrics.metric.ResourceCallTimer;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.function.Consumer;

public class InstrumentedJettyServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentedJettyServer.class);

    private final MetricService metricService;

    private final Consumer<HttpChannel<?>> handler;

    private final HttpCallTimer timer;

    /**
     * Create a new instrumented jetty service.
     *
     * @param metricService The metric service to use.
     * @param threadPool Thread pool to provide to jetty server.
     * @param channelHandler Channel handler to pass off requests to post-metric collection.
     */
    public InstrumentedJettyServer(final MetricService metricService,
                                   final ThreadPool threadPool,
                                   final Consumer<HttpChannel<?>> channelHandler) {

        super(threadPool);
        this.metricService = metricService;
        this.timer = metricService.createHttpResourceCallTimer(MetricGroup.SERVER_HTTP, "http-response");
        this.handler = channelHandler;
    }

    @Override
    public void handle(final HttpChannel<?> channel) throws IOException, ServletException {
        LOG.info("Collecting metrics on jetty https request");

        final String path = channel.getRequest().getRequestURI();
        final String method = channel.getRequest().getMethod();

        final ResourceCallTimer.StopWatch requestStopwatch = timer.start(Direction.INBOUND, HttpResource.fromUrl(path), HttpAction.fromString(method));

        // Pass the channel down to our application.
        try {
            handler.accept(channel);
        } finally {
            requestStopwatch.stop(Status.fromValue(channel.getResponse().getStatus()));
        }
    }

    @Override
    public void handleAsync(final HttpChannel<?> channel) throws IOException, ServletException {
        handler.accept(channel);
    }

    @Override
    public void doStart() throws Exception {
        super.doStart();
    }

}
