package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.codahale.metrics.MetricRegistry.name;

public class BpMetricSet {

    private static final Logger LOG = LoggerFactory.getLogger(BpMetricSet.class);

    private final Map<String, BpMetric> metrics = new ConcurrentHashMap<>();

    /**
     * Register a new metric into the set.
     * @param metric Metric to add to the set.
     * @param <M> The metric type.
     * @return The metric that's been added.
     */
    public <M extends BpMetric> M registerMetric(final M metric) {
        return (M) this.metrics.computeIfAbsent(metric.getName(), (name) -> {
                LOG.info("Registering new metric: {}", name);
                return metric;
            });
    }

    public BpMetric getMetric(final Class owningClass, final String metricName) {
        return getMetricByFullName(name(owningClass.getName(), metricName));
    }

    public void removeMetric(final Class owningClass, final String name) {
        removeMetric(name(owningClass.getName(), name));
    }

    public void removeMetric(final String fullName) {
        this.metrics.remove(fullName);
    }

    public void clear() {
        metrics.clear();
    }

    private BpMetric getMetricByFullName(final String name) {
        return metrics.get(name);
    }

    /**
     * Sample the current metric set.
     * @return The current metric aggregateSample set mapped to each metrics name.
     */
    public Map<String, BpMetric.Sample> sample() {
        return this.metrics.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry) -> entry.getValue().aggregateSample()));
    }

    public BpMetric.Sample sample(final Class owningClass, final String metricName) {
        return sample(name(owningClass, metricName));
    }

    public BpMetric.Sample sample(final String fullName) {
        return this.metrics.get(fullName).aggregateSample();
    }
}
