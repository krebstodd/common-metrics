package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.metric.BusinessEventRepository;
import com.blispay.common.metrics.metric.DatasourceCallTimer;
import com.blispay.common.metrics.metric.HttpCallTimer;
import com.blispay.common.metrics.metric.InternalResourceCallTimer;
import com.blispay.common.metrics.metric.MqCallTimer;
import com.blispay.common.metrics.metric.ResourceCounter;
import com.blispay.common.metrics.metric.ResourceUtilizationGauge;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.business.EventFactory;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallMetricFactory;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetricFactory;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallMetricFactory;
import com.blispay.common.metrics.model.call.mq.MqResourceCallMetricFactory;
import com.blispay.common.metrics.model.counter.ResourceCounterMetricFactory;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetricFactory;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.report.SnapshotReporter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MetricService {

    private static final MetricService GLOBAL = new MetricService();

    private final EventDispatcher eventDispatcher = new EventDispatcher();
    private final Set<SnapshotProvider> snapshotProviders = new HashSet<>();
    private final Set<SnapshotReporter> snapshotReporters = new HashSet<>();

    public ResourceCounter createResourceCounter(final MetricGroup group, final String name) {
        final ResourceCounterMetricFactory factory = new ResourceCounterMetricFactory(group, name);
        return new ResourceCounter(eventDispatcher.newEventEmitter(), factory);
    }

    public ResourceUtilizationGauge createResourceUtilizationGauge(final MetricGroup group, final String name,
                                                                   final Supplier<ResourceUtilizationData> rGauge) {

        final EventEmitter emitter = eventDispatcher.newEventEmitter();
        final ResourceUtilizationMetricFactory factory = new ResourceUtilizationMetricFactory(group, name);
        final ResourceUtilizationGauge gauge = new ResourceUtilizationGauge(emitter, factory, rGauge);

        snapshotProviders.add(gauge);

        return gauge;

    }

    public <T> BusinessEventRepository<T> createBusinessEventRepository(final MetricGroup group, final String name) {
        return new BusinessEventRepository<>(eventDispatcher.newEventEmitter(), new EventFactory(group, name));
    }

    public HttpCallTimer createHttpResourceCallTimer(final MetricGroup group, final String name) {
        return new HttpCallTimer(eventDispatcher.newEventEmitter(), new HttpResourceCallMetricFactory(group, name));
    }

    public DatasourceCallTimer createDataSourceCallTimer(final MetricGroup group, final String name) {
        return new DatasourceCallTimer(eventDispatcher.newEventEmitter(), new DataSourceResourceCallMetricFactory(group, name));
    }

    public InternalResourceCallTimer createInternalResourceCallTimer(final MetricGroup group, final String name) {
        return new InternalResourceCallTimer(eventDispatcher.newEventEmitter(), new InternalResourceCallMetricFactory(group, name));
    }

    public MqCallTimer createMqResourceCallTimer(final MetricGroup group, final String name) {
        return new MqCallTimer(eventDispatcher.newEventEmitter(), new MqResourceCallMetricFactory(group, name));
    }

    public void addEventListener(final EventSubscriber eventListener) {
        this.eventDispatcher.addListener(eventListener);
    }

    public void addSnapshotReporter(final SnapshotReporter snReporter) {
        snReporter.setSnapshotProviders(() -> snapshotProviders);
        snapshotReporters.add(snReporter);
    }

    public static MetricService globalInstance() {
        return GLOBAL;
    }

}
