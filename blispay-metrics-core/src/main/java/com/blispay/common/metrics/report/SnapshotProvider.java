package com.blispay.common.metrics.report;

import com.blispay.common.metrics.event.MetricEvent;

public interface SnapshotProvider {

    MetricEvent snapshot();

}
