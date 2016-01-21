package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

import java.util.HashSet;
import java.util.Set;

public class DefaultBpEventRecordingService implements BpEventRecordingService {

    private final Set<BpEventReporter> eventReporters = new HashSet<>();

    @Override
    public void recordEvent(final RecordableEvent event) {
        eventReporters.forEach(reporter -> reporter.reportEvent(event));
    }

    @Override
    public void addEventReporter(final BpEventReporter reporter) {
        eventReporters.add(reporter);
    }

    @Override
    public void removeEventReporter(final BpEventReporter reporter) {
        eventReporters.remove(reporter);
    }
}
