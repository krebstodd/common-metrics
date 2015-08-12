package com.blispay.common.metrics;

import java.util.Map;

public interface BpMetricSet {

    Map<String, BpMetric> getMetrics();

}
