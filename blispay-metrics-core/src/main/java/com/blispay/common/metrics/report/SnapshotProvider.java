package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.BaseMetricModel;

public interface SnapshotProvider {

    BaseMetricModel snapshot();

}
