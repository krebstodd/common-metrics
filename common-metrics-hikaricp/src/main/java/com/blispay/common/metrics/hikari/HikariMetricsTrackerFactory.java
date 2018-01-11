package com.blispay.common.metrics.hikari;

import com.blispay.common.metrics.MetricService;
import com.zaxxer.hikari.metrics.MetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;

/**
 * Hikari CP allows users to provide their own implementation of {@link MetricsTrackerFactory} in order to receive
 * performance statistics related to each connection pool managed by Hikari.
 */
public class HikariMetricsTrackerFactory implements MetricsTrackerFactory {

    private final MetricService metricService;

    /**
     * Create a new factory instance.
     *
     * @param metricService Metric service against which Hikari metrics should be published.
     */
    public HikariMetricsTrackerFactory(final MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public MetricsTracker create(final String poolName, final PoolStats poolStats) {
        return HikariMetricsTracker.create()
                .forPoolName(poolName)
                .withPoolStats(poolStats)
                .publishTo(metricService)
                .build();
    }
}
