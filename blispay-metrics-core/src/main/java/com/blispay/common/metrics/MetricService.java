package com.blispay.common.metrics;

import com.blispay.common.metrics.event.DefaultEventDispatcher;
import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.metric.EventRepository;
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
                                                                   final Supplier<ResourceUtilizationData> resourceGauge) {

        return createResourceUtilizationGauge(group, name, resourceGauge, Boolean.TRUE);
    }

    /**
     * Create a new resource utilization gauge with a custom utilization data provider.
     *
     * @param group The gauge's metric group.
     * @param name The gauge's metric name.
     * @param resourceGauge A supplier for utilization data.
     * @param allowSnapshots Allow snapshot reporters registered to this service instance to report on this guage.
     * @return a resource utilization gauge configured as requested.
     */
    public ResourceUtilizationGauge createResourceUtilizationGauge(final MetricGroup group, final String name,
                                                                   final Supplier<ResourceUtilizationData> resourceGauge,
                                                                   final Boolean allowSnapshots) {

        final EventEmitter emitter = eventDispatcher.newEventEmitter();
        final ResourceUtilizationMetricFactory factory = new ResourceUtilizationMetricFactory(applicationId, group, name);
        final ResourceUtilizationGauge gauge = new ResourceUtilizationGauge(emitter, factory, resourceGauge);

        if (allowSnapshots) {
            snapshotProviders.add(gauge);
        }

        return gauge;

    }

    public <T> EventRepository<T> createEventRepository(final MetricGroup group, final String name) {
        return new EventRepository<>(eventDispatcher.newEventEmitter(), new EventFactory<T>(applicationId, group, name));
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

    /**
     * Add a new snapshot reporting implementation interested in reporting on events in this service. Starts the reporter.
     *
     * @param snReporter Snapshot reporter instance.
     */
    public void addSnapshotReporter(final SnapshotReporter snReporter) {
        LOG.info("Adding new snapshot reporter [{}].", snReporter.getClass().getName());

        snReporter.setSnapshotProviders(() -> snapshotProviders);
        snReporter.start();
        snapshotReporters.add(snReporter);
    }

    /**
     * Add a new metric probe. Probe life cycles are managed by the metric service. It is not mandatory that a probe be added
     * to the service, it is for the convenience of the app developer.
     *
     * @param probe Metric probe to manage
     */
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

    /**
     * Retrieve the global instance of the metric service. Allows clients to access the service being used by an application
     * to register their own metrics.
     *
     * @return The global metrics service instance.
     */
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
    public void stop() {
        stop(() -> { });
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
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public int getPhase() {
        return StartupPhase.SERVICE.value();
    }
}
