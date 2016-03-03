package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.EventModel;

public interface SnapshotProvider {

    EventModel snapshot();

}
