package com.blispay.common.metrics;

import com.blispay.common.metrics.event.DefaultEventDispatcher;
import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventSubscriber;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.report.SnapshotReporter;
import com.blispay.common.metrics.transaction.TransactionFactory;
import com.blispay.common.metrics.util.StartupPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class MetricService.
 */
public class MetricService implements SmartLifecycle {

    private static final MetricService GLOBAL = new MetricService("metrics");
    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    private final EventDispatcher eventDispatcher;
    private final Set<SnapshotProvider> snapshotProviders = new HashSet<>();
    private final Set<SnapshotReporter> snapshotReporters = new HashSet<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);
    private String applicationId;

    /**
     * Constructs MetricService.
     *
     * @param applicationId applicationId.
     */
    public MetricService(final String applicationId) {
        this(applicationId, new DefaultEventDispatcher());
    }

    /**
     * Constructs MetricService.
     *
     * @param applicationId applicationId.
     * @param eventDispatcher eventDispatcher.
     */
    public MetricService(final String applicationId, final EventDispatcher eventDispatcher) {
        this.applicationId = applicationId;
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Method eventFactory.
     *
     * @param hint hint.
     * @param <U> Event data type
     * @return return value.
     */
    public <U> EventFactory.Builder<U> eventFactory(final Class<U> hint) {
        return new EventFactory.Builder<>(hint, applicationId, eventDispatcher.newEventEmitter());
    }

    /**
     * Method transactionFactory.
     *
     * @return return value.
     */
    public TransactionFactory.Builder transactionFactory() {
        return new TransactionFactory.Builder(applicationId, eventDispatcher.newEventEmitter());
    }

    /**
     * Method utilizationGauge.
     *
     * @return return value.
     */
    public UtilizationGauge.Builder utilizationGauge() {
        return new UtilizationGauge.Builder(applicationId, snapshotProviders::add);
    }

    /**
     * Method stateMonitor.
     *
     * @return return value.
     */
    public StateMonitor.Builder stateMonitor() {
        return new StateMonitor.Builder(applicationId, snapshotProviders::add);
    }

    /**
     * Method resourceCounter.
     *
     * @return return value.
     */
    public ResourceCounter.Builder resourceCounter() {
        return new ResourceCounter.Builder(applicationId, eventDispatcher.newEventEmitter());
    }

    /**
     * Method gauge.
     *
     * @param hint hint.
     * @param <U> Gauge data type.
     * @return return value.
     */
    public <U> Gauge.Builder<U> gauge(final Class<U> hint) {
        return new Gauge.Builder<>(hint, applicationId, snapshotProviders::add);
    }

    /**
     * Method removeSnapshotProvider.
     *
     * @param provider provider.
     */
    public void removeSnapshotProvider(final SnapshotProvider provider) {
        this.snapshotProviders.remove(provider);
    }

    /**
     * Method addEventSubscriber.
     *
     * @param eventListener eventListener.
     */
    public void addEventSubscriber(final EventSubscriber eventListener) {
        this.eventDispatcher.subscribe(eventListener);
    }

    /**
     * Method removeEventSubscriber.
     *
     * @param eventSubscriber eventSubscriber.
     */
    public void removeEventSubscriber(final EventSubscriber eventSubscriber) {
        this.eventDispatcher.unSubscribe(eventSubscriber);
    }

    /**
     * Add a new snapshot reporting implementation interested in reporting on events in this service. Starts the reporter.
     *
     * @param snReporter Snapshot reporter instance.
     */
    public void addSnapshotReporter(final SnapshotReporter snReporter) {
        LOG.info("Adding new snapshot reporter [{}].", snReporter.getClass().getName());

        snReporter.setSnapshotProviders(() -> snapshotProviders);

        if (!snReporter.isRunning()) {
            snReporter.start();
        }

        snapshotReporters.add(snReporter);
    }

    /**
     * Method removeSnapshotReporter.
     *
     * @param reporter reporter.
     */
    public void removeSnapshotReporter(final SnapshotReporter reporter) {
        this.snapshotReporters.remove(reporter);
        reporter.stop();
    }

    /**
     * Method getApplicationId.
     *
     * @return return value.
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Method setApplicationId.
     *
     * @param appId appId.
     */
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
        return GLOBAL;
    }

    /**
     * Set the global app id and return the global instance.
     * @param name App id
     * @return global instance.
     */
    public static MetricService globalInstance(final String name) {
        setGlobalAppId(name);
        return globalInstance();
    }

    /**
     * Method setGlobalAppId.
     *
     * @param globalAppId globalAppId.
     */
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

            snapshotReporters.forEach(SnapshotReporter::stop);
            eventDispatcher.stop(runnable);

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
