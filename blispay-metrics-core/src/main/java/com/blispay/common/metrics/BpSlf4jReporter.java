package com.blispay.common.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        while (iter.hasNext()) {
            metricLogger.info(((BpMetric) iter.next()).sample().toString());
        }
    }

    @Override
    public void registerMetric(final BpMetric metric) {
        metrics.put(metric.getName(), metric);
    }

    @Override
    public void unregisterMetric(final String metric) {
        metrics.remove(metric);
    }

}