package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.metric.InfrastructureMetricName;
import com.blispay.common.metrics.metric.Measurement;
import com.blispay.common.metrics.metric.MetricClass;
import com.codahale.metrics.RatioGauge;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.function.Consumer;

public class JettyProbe {

    private final Server instrumentedServer;

    private ConnectionFactory instrumentedConnectionFactory;

    private final BpMetricService metricService;
    private final String appId;

    /**
     * Probe jetty for metrics around connections usage, thread usage, and generatl http metrics.
     *
     * @param applicationId The applications unique identifier.
     * @param threadPool Thread pool to be used to serve http requests.
     * @param channelHandler Channel handler being handed to http server.
     * @param metricService Metric service to register probe on.
     */
    public JettyProbe(final String applicationId,
                      final QueuedThreadPool threadPool,
                      final Consumer<HttpChannel<?>> channelHandler,
                      final BpMetricService metricService) {
        this.appId = applicationId;
        this.metricService = metricService;
        this.instrumentedServer = new InstrumentedJettyServer(applicationId, metricService, threadPool, channelHandler);
        instrumentThreadPool(metricService, threadPool);
    }

    public Server getInstrumentedServer() {
        return instrumentedServer;
    }

    public ConnectionFactory getInstrumentedConnectionFactory() {
        return instrumentedConnectionFactory;
    }

    public ConnectionFactory setAndInstrumentConnectionFactory(final ConnectionFactory factory) {
        this.instrumentedConnectionFactory = instrumentConnectionFactory(metricService, factory);
        return this.instrumentedConnectionFactory;
    }

    /**
     * Instrument a jetty connection factory to collect behavioral metrics on the amount of time connections spend open.
     *
     * @param plain Plain jetty connection factory.
     * @return Instrumented facotry.
     */
    private ConnectionFactory instrumentConnectionFactory(final BpMetricService metricService, final ConnectionFactory plain) {
        final BpTimer connectionTimer = metricService.createTimer(new InfrastructureMetricName(appId, "jetty", "connection", "time"), MetricClass.executionTime());
        return new InstrumentedConnectionFactory(plain, connectionTimer);
    }

    /**
     * Create thread pool related JMX metrics for the provided thread pool.
     *
     * @param pool Queued thread pool to profile.
     */
    private void instrumentThreadPool(final BpMetricService metricService, final QueuedThreadPool pool) {

        metricService.createGauge(new InfrastructureMetricName(appId, "jetty", "threadPool", "utilization"), MetricClass.threadPool(), Measurement.Units.PERCENTAGE,
                () -> RatioGauge.Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getThreads()).getValue());

        metricService.createGauge(new InfrastructureMetricName(appId, "jetty", "threadPool", "utilization-max"), MetricClass.threadPool(), Measurement.Units.PERCENTAGE,
                () -> RatioGauge.Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getMaxThreads()).getValue());

        metricService.createGauge(new InfrastructureMetricName(appId, "jetty", "threadPool", "threadCount"), MetricClass.threadPool(), Measurement.Units.TOTAL, pool::getThreads);
        metricService.createGauge(new InfrastructureMetricName(appId, "jetty", "threadPool", "queueSize"), MetricClass.threadPool(), Measurement.Units.TOTAL, pool::getQueueSize);

    }


}
