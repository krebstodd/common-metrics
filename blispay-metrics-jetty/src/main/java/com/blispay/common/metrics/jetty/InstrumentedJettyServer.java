package com.blispay.common.metrics.jetty;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.Transaction;
import com.blispay.common.metrics.TransactionFactory;
import com.blispay.common.metrics.model.EventGroup;
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

    private final Consumer<HttpChannel<?>> handler;

    private final TransactionFactory txFactory;

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

        this.txFactory = metricService.transactionFactory()
                .inGroup(EventGroup.SERVER_HTTP)
                .withName("http-response")
                .inDirection(Direction.INBOUND)
                .build();

        this.handler = channelHandler;
    }

    @Override
    public void handle(final HttpChannel<?> channel) throws IOException, ServletException {
        LOG.info("Collecting metrics on jetty https request");

        final String path = channel.getRequest().getRequestURI();
        final String method = channel.getRequest().getMethod();


        final Transaction tx = txFactory.create()
                .onResource(HttpResource.fromUrl(path))
                .withAction(HttpAction.fromString(method))
                .start();

        // Pass the channel down to our application.
        try {
            handler.accept(channel);
        } finally {
            tx.stop(Status.fromValue(channel.getResponse().getStatus()));
        }
    }

    @Override
    public void handleAsync(final HttpChannel<?> channel) throws IOException, ServletException {
        handler.accept(channel);
    }

}
