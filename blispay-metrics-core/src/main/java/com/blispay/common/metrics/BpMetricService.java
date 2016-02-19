package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventListener;
import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHealthCheck;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.metric.Measurement;
import com.blispay.common.metrics.metric.MetricName;
import com.blispay.common.metrics.metric.MetricClass;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.report.SnapshotReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BpMetricService {

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricService.class);

    private static final BpMetricService GLOBAL = new BpMetricService();

    private final EventDispatcher eventDispatcher = new EventDispatcher();
    private final ConcurrentHashMap<String, BpMetric> metrics = new ConcurrentHashMap<>();
    private final Set<SnapshotReporter> snapshotReporters = new HashSet<>();

    public BpCounter createCounter(final MetricName name, final MetricClass type) {
        return addMetric(new BpCounter(name, type));
    }

    public BpTimer createTimer(final MetricName name, final MetricClass type) {
        return addMetric(new BpTimer(name, type));
    }

    public <R> BpGauge<R> createGauge(final MetricName name, final MetricClass type, final Measurement.Units supplierUnits,
                                      final Supplier<R> resultSupplier) {

        return addMetric(new BpGauge<>(name, type, resultSupplier, supplierUnits));
    }

    public BpHealthCheck createHealthCheck(final MetricName name, final MetricClass type, final Supplier<BpHealthCheck.Result> healthSupplier) {

        return addMetric(new BpHealthCheck(name, type, healthSupplier));
    }

    public <M extends BpMetric> M addMetric(final M metric) {
        return (M) metrics.computeIfAbsent(metric.getName().toString(), nameStr -> {
                LOG.info("Registering new metric [{}]", nameStr);
                metric.setEventEmitter(eventDispatcher.newEventEmitter());
                return metric;
            });
    }

    public void addEventListener(final EventListener eventListener) {
        this.eventDispatcher.addListener(eventListener);
    }

    public void addSnapshotReporter(final SnapshotReporter snReporter) {
        snReporter.setSnapshotProviders(this::getSnapshotProviders);
        snapshotReporters.add(snReporter);
    }

    public BpMetric getMetric(final String name) {
        return metrics.get(name);
    }

    public Set<SnapshotProvider> getSnapshotProviders() {
        return metrics.values()
                .stream()
                .filter(met -> met instanceof SnapshotProvider)
                .map(met -> (SnapshotProvider) met)
                .collect(Collectors.toSet());
    }

    public static BpMetricService globalInstance() {
        return GLOBAL;
    }

}
