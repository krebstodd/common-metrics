package com.blispay.common.metrics;

import com.codahale.metrics.RatioGauge;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class JettyProbe extends BpMetricProbe {

    private static final Logger LOG = LoggerFactory.getLogger(JettyProbe.class);

    private final Server instrumentedServer;

    private ConnectionFactory instrumentedConnectionFactory;

    private final ThreadPool instrumentedThreadPool;

    /**
     * Probe jetty for metrics around connections usage, thread usage, and generatl http metrics.
     *
     * @param threadPool Thread pool to be used to serve http requests.
     * @param channelHandler Channel handler being handed to http server.
     */
    public JettyProbe(final QueuedThreadPool threadPool,
                      final Consumer<HttpChannel<?>> channelHandler) {
        this.instrumentedThreadPool = instrumentThreadPool(threadPool);
        this.instrumentedServer = new InstrumentedJettyServer(metricService, threadPool, channelHandler);
    }

    public Server getInstrumentedServer() {
        return instrumentedServer;
    }

    public ConnectionFactory getInstrumentedConnectionFactory() {
        return instrumentedConnectionFactory;
    }

    public ThreadPool getInstrumentedThreadPool() {
        return instrumentedThreadPool;
    }

    public ConnectionFactory setAndInstrumentConnectionFactory(final ConnectionFactory factory) {
        this.instrumentedConnectionFactory = instrumentConnectionFactory(factory);
        return this.instrumentedConnectionFactory;
    }

    @Override
    protected void startProbe() { }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /**
     * Instrument a jetty connection factory to collect behavioral metrics on the amount of time connections spend open.
     *
     * @param plain Plain jetty connection factory.
     * @return Instrumented facotry.
     */
    private ConnectionFactory instrumentConnectionFactory(final ConnectionFactory plain) {
        final BpTimer connectionTimer
                = metricService.createTimer(InstrumentedConnectionFactory.class, "connectionTimer", "Gather metrics on the amount of time a Jetty Connection remains open.");
        return new InstrumentedConnectionFactory(plain, connectionTimer);
    }

    /**
     * Create thread pool related JMX metrics for the provided thread pool.
     *
     * @param pool Queued thread pool to profile.
     */
    private ThreadPool instrumentThreadPool(final QueuedThreadPool pool) {

        metricService.createGauge(QueuedThreadPool.class, "utilization", "Utilization percentage of the jetty thread pool.",
                () -> RatioGauge.Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getThreads()).getValue());

        metricService.createGauge(QueuedThreadPool.class, "utilization-max", "Maximum utilization percentage of the jetty thread pool.",
                () ->  RatioGauge.Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getMaxThreads()));

        metricService.createGauge(QueuedThreadPool.class, "size", "The current size of the thread pool.",
                () -> pool.getThreads());

        metricService.createGauge(QueuedThreadPool.class, "jobs", "The current number of jobs waiting in the thread pool queue.",
                () -> pool.getQueueSize());

        return pool;

    }


}
