package com.blispay.common.metrics.report;

import com.blispay.common.metrics.metric.BpMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BpSlf4jReporter extends BpScheduledReporter {

    private final Logger metricLogger;

    public BpSlf4jReporter(final String loggerName, final Integer frequency, final TimeUnit frequencyUnits) {
        super(frequency, frequencyUnits);
        this.metricLogger = LoggerFactory.getLogger(loggerName);
    }

    private void logSample(final BpMetric.Sample sample) {
        metricLogger.info(sample.toString());
    }

    @Override
    public void report() {
        sampleMetrics().entrySet().stream().map(Map.Entry::getValue).forEach(this::logSample);
    }

}
