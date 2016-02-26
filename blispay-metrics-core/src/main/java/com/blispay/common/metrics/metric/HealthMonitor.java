package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.health.HealthCheckData;
import com.blispay.common.metrics.model.health.HealthCheckMetricFactory;
import com.blispay.common.metrics.report.SnapshotProvider;

import java.util.function.Supplier;

public class HealthMonitor extends MetricRepository implements SnapshotProvider {

    private HealthCheckMetricFactory metricFactory;

    private final Supplier<HealthCheckData> stateSupplier;

    /**
     * Gauge for reporting resource health.
     *
     * @param eventEmitter Event emitter instance for emitting event metrics.
     * @param factory Factory for producing immutable resource metrics.
     * @param stateSupplier Supplies current resource health stats on demand.
     */
    public HealthMonitor(final EventEmitter eventEmitter,
                         final HealthCheckMetricFactory factory,
                         final Supplier<HealthCheckData> stateSupplier) {

        super(factory.getGroup(), factory.getName(), eventEmitter);

        this.metricFactory = factory;
        this.stateSupplier = stateSupplier;
    }

    @Override
    public BaseMetricModel snapshot() {
        return metricFactory.newMetric(stateSupplier.get());
    }

    @Override
    public boolean equals(final Object other) {
        return computeEquals(this, other);
    }

    @Override
    public int hashCode() {
        return computeHashCode(this);
    }

}
