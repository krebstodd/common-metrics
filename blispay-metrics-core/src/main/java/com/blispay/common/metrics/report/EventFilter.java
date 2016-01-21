package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

public interface EventFilter {

    Boolean shouldReportEvent(RecordableEvent event);

}
