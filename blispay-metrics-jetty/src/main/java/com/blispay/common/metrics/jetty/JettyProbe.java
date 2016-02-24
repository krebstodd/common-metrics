package com.blispay.common.metrics.jetty;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.metric.MetricProbe;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.function.Consumer;

public class JettyProbe implements MetricProbe {

    private final Server instrumentedServer;

    /**
     * Probe jetty for metrics around connections usage, thread usage, and generatl http metrics.
     *
     * @param threadPool Thread pool to be used to serve http requests.
     * @param channelHandler Channel handler being handed to http server.
     * @param metricService Metric service to register probe on.
     */
    public JettyProbe(final QueuedThreadPool threadPool,
                      final Consumer<HttpChannel<?>> channelHandler,
                      final MetricService metricService) {

        this.instrumentedServer = new InstrumentedJettyServer(metricService, threadPool, channelHandler);
        instrumentThreadPool(metricService, threadPool);
    }

    public Server getInstrumentedServer() {
        return instrumentedServer;
    }

    /**
     * Create thread pool related JMX metrics for the provided thread pool.
     *
     * @param pool Queued thread pool to profile.
     */
    private static void instrumentThreadPool(final MetricService metricService, final QueuedThreadPool pool) {

        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "jetty-thread-pool", () -> {
                final Long minThreads = 0L;
                final Long maxThreads = Long.valueOf(pool.getMaxThreads());
                final Long currThreads = Long.valueOf(pool.getThreads() - pool.getIdleThreads());
                final Double pctUtil = (double) currThreads / maxThreads;

                return new ResourceUtilizationData(minThreads, maxThreads, currThreads, pctUtil);
            });

    }

}
