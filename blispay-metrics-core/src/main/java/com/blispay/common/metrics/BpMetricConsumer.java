package com.blispay.common.metrics;

public interface BpMetricConsumer {

    void registerMetric(BpMetric metric);

    void unregisterMetric(String metric);

    void start();

    void stop();

}
