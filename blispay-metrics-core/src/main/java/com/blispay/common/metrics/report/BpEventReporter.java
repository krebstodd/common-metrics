package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

public interface BpEventReporter {

    void reportEvent(final RecordableEvent event);

}
