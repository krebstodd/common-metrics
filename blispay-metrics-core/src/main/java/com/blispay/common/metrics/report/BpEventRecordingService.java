package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

public interface BpEventRecordingService {

    default void recordEvent(final RecordableEvent event) {

    }

    default void addEventReporter(final BpEventReporter reporter) {

    }

    default void removeEventReporter(final BpEventReporter reporter) {

    }

}
