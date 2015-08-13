package com.blispay.common.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BpSlf4jReporter extends BpScheduledReporter implements BpMetricConsumer {

    private final Map<String, BpMetric> metrics = new ConcurrentHashMap<>();

    private final Logger metricLogger;

    public BpSlf4jReporter(final String loggerName, final Integer frequency, final TimeUnit frequencyUnits) {
        super(frequency, frequencyUnits);
        this.metricLogger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void report() {
        final Iterator iter = metrics.values().iterator();
        final Long sampleTime = Instant.now().toEpochMilli();

        metricLogger.info("Start sample: {}", sampleTime);
        while (iter.hasNext()) {
            logMetric((BpMetric) iter.next());
        }
        metricLogger.info("Stop sample: {}", sampleTime);
    }

    @Override
    public void registerMetric(final BpMetric metric) {
        // TODO: This isn't thread safe.
        if (metrics.containsKey(metric.getName())) {
            throw new IllegalArgumentException("BpSlf4jReporter already contains metric " + metric.getName());
        }

        metrics.put(metric.getName(), metric);
    }

    @Override
    public void unregisterMetric(final String metric) {
        metrics.remove(metric);
    }

    private void logMetric(final BpMetric metric) {

        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(metric.getName()).append(",");

        final ImmutablePair[] sample = metric.sample();
        ImmutablePair current;
        for (int i = 0; i < sample.length; i++) {
            current = sample[i];
            sb.append(current.getKey()).append("=").append(current.getVal()).append(",");
        }

        metricLogger.info(sb.toString());

    }


}
