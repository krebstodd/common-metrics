package com.blispay.common.metrics;

interface BpMetricReporter {

    void registerMetric(BpMetric metric);

    void unregisterMetric(String metric);

    void start();

    void stop();

}
