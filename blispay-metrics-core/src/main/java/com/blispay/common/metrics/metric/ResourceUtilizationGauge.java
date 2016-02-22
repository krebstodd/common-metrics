package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetricFactory;
import com.blispay.common.metrics.report.SnapshotProvider;

import java.util.function.Supplier;

public class ResourceUtilizationGauge extends MetricRepository implements SnapshotProvider {

    private ResourceUtilizationMetricFactory metricFactory;

    private final Supplier<ResourceUtilizationData> stateSupplier;

    public ResourceUtilizationGauge(final EventEmitter eventEmitter,
                                    final ResourceUtilizationMetricFactory factory,
                                    final Supplier<ResourceUtilizationData> stateSupplier) {
        super(eventEmitter);

        this.metricFactory = factory;
        this.stateSupplier = stateSupplier;
    }

    @Override
    public BaseMetricModel snapshot() {
        return metricFactory.newMetric(stateSupplier.get());
    }

}