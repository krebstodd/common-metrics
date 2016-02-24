package com.blispay.common.metrics;

import com.blispay.common.metrics.event.DefaultEventDispatcher;
import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.metric.BusinessEventRepository;
import com.blispay.common.metrics.metric.DatasourceCallTimer;
import com.blispay.common.metrics.metric.HttpCallTimer;
import com.blispay.common.metrics.metric.InternalResourceCallTimer;
import com.blispay.common.metrics.metric.MetricProbe;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MetricService implements SmartLifecycle {

    private static final MetricService GLOBAL = new MetricService(null);
    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    private final EventDispatcher eventDispatcher;
    private final Set<SnapshotProvider> snapshotProviders = new HashSet<>();
    private final Set<SnapshotReporter> snapshotReporters = new HashSet<>();
    private final List<MetricProbe> probes = new LinkedList<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);
    private String applicationId;

    public MetricService(final String applicationId) {
        this(applicationId, new DefaultEventDispatcher());
    }

    public MetricService(final String applicationId, final EventDispatcher eventDispatcher) {
        this.applicationId = applicationId;
        this.eventDispatcher = eventDispatcher;
    }

    public ResourceCounter createResourceCounter(final MetricGroup group, final String name) {
        final ResourceCounterMetricFactory factory = new ResourceCounterMetricFactory(applicationId, group, name);
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
        final ResourceUtilizationMetricFactory factory = new ResourceUtilizationMetricFactory(applicationId, group, name);
        final ResourceUtilizationGauge gauge = new ResourceUtilizationGauge(emitter, factory, rGauge);

        if (allowSnapshots) {
            snapshotProviders.add(gauge);
        }

        return gauge;

    }

    public <T> BusinessEventRepository<T> createBusinessEventRepository(final MetricGroup group, final String name) {
        return new BusinessEventRepository<>(eventDispatcher.newEventEmitter(), new EventFactory(applicationId, group, name));
    }

    public HttpCallTimer createHttpResourceCallTimer(final MetricGroup group, final String name) {
        return new HttpCallTimer(eventDispatcher.newEventEmitter(), new HttpResourceCallMetricFactory(applicationId, group, name));
    }

    public DatasourceCallTimer createDataSourceCallTimer(final MetricGroup group, final String name) {
        return new DatasourceCallTimer(eventDispatcher.newEventEmitter(), new DataSourceResourceCallMetricFactory(applicationId, group, name));
    }

    public InternalResourceCallTimer createInternalResourceCallTimer(final MetricGroup group, final String name) {
        return new InternalResourceCallTimer(eventDispatcher.newEventEmitter(), new InternalResourceCallMetricFactory(applicationId, group, name));
    }

    public MqCallTimer createMqResourceCallTimer(final MetricGroup group, final String name) {
        return new MqCallTimer(eventDispatcher.newEventEmitter(), new MqResourceCallMetricFactory(applicationId, group, name));
    }

    public void addEventSubscriber(final EventSubscriber eventListener) {
        this.eventDispatcher.subscribe(eventListener);
    }

    public void addSnapshotReporter(final SnapshotReporter snReporter) {
        LOG.info("Adding new snapshot reporter [{}].", snReporter.getClass().getName());

        snReporter.setSnapshotProviders(() -> snapshotProviders);
        snReporter.start();
        snapshotReporters.add(snReporter);
    }

    public void addProbe(final MetricProbe probe) {
        LOG.info("Adding metric probe [{}].", probe.getClass().getName());

        if (isRunning()) {
            probe.start();
        }

        probes.add(probe);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final String appId) {
        this.applicationId = appId;
    }

    public static MetricService globalInstance() {
        if (GLOBAL.getApplicationId() == null) {
            throw new IllegalStateException("Must set global app id before accessing global instance");
        }

        return GLOBAL;
    }

    public static void setGlobalAppId(final String globalAppId) {
        GLOBAL.setApplicationId(globalAppId);
    }

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public void stop(final Runnable runnable) {

        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {

            LOG.info("Stopping metric service...");

            eventDispatcher.stop(runnable);
            snapshotReporters.forEach(SnapshotReporter::stop);
            probes.forEach(MetricProbe::stop);
        } else {
            LOG.warn("Metric service is already stopped");
        }

        runnable.run();
        LOG.info("Metric service stopped.");

    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            LOG.info("Starting metric service...");

            eventDispatcher.start();
            probes.forEach(MetricProbe::start);
        } else {
            LOG.warn("Metric service is already running.");
        }

        LOG.info("Metric service started.");

    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {
            LOG.info("Stopping metric service...");

            eventDispatcher.stop();
            snapshotReporters.forEach(SnapshotReporter::stop);
            probes.forEach(MetricProbe::stop);

        } else {
            LOG.warn("Metric service is already stopped.");
        }

        LOG.info("Metric service stopped.");
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
