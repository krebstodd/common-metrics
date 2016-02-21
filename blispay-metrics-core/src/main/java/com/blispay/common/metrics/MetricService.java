package com.blispay.common.metrics;

import com.blispay.common.metrics.event.DefaultEventDispatcher;
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
import com.blispay.common.metrics.util.StartupPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MetricService implements SmartLifecycle {

    private static final MetricService GLOBAL = new MetricService();

    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    private final EventDispatcher eventDispatcher;
    private final Set<SnapshotProvider> snapshotProviders = new HashSet<>();
    private final Set<SnapshotReporter> snapshotReporters = new HashSet<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    public MetricService() {
        this(new DefaultEventDispatcher());
    }

    public MetricService(final EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public ResourceCounter createResourceCounter(final MetricGroup group, final String name) {
        final ResourceCounterMetricFactory factory = new ResourceCounterMetricFactory(group, name);
        return new ResourceCounter(eventDispatcher.newEventEmitter(), factory);
    }

    public ResourceUtilizationGauge createResourceUtilizationGauge(final MetricGroup group, final String name,
                                                                   final Supplier<ResourceUtilizationData> rGauge) {

        return createResourceUtilizationGauge(group, name, rGauge, Boolean.TRUE);
    }

    public ResourceUtilizationGauge createResourceUtilizationGauge(final MetricGroup group, final String name,
                                                                   final Supplier<ResourceUtilizationData> rGauge,
                                                                   final Boolean allowSnapshots) {

        final EventEmitter emitter = eventDispatcher.newEventEmitter();
        final ResourceUtilizationMetricFactory factory = new ResourceUtilizationMetricFactory(group, name);
        final ResourceUtilizationGauge gauge = new ResourceUtilizationGauge(emitter, factory, rGauge);

        if (allowSnapshots) {
            snapshotProviders.add(gauge);
        }

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

    public void addEventSubscriber(final EventSubscriber eventListener) {
        this.eventDispatcher.subscribe(eventListener);
    }

    public void addSnapshotReporter(final SnapshotReporter snReporter) {
        snReporter.setSnapshotProviders(() -> snapshotProviders);
        snapshotReporters.add(snReporter);
    }

    public static MetricService globalInstance() {
        return GLOBAL;
    }

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public void stop(final Runnable runnable) {

        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            eventDispatcher.stop(runnable);

        } else {
            LOG.warn("Metric service is already stopped");
            runnable.run();
        }

    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {

            eventDispatcher.start();

        } else {
            LOG.warn("Metric service is already running.");
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            eventDispatcher.stop();

        } else {
            LOG.warn("Metric service is already stopped.");
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public int getPhase() {
        return StartupPhase.SERVICE.value();
    }
}
