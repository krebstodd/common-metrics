package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class BpEventRecordingService {

    private final Set<BpEventReporter> eventReporters = new HashSet<>();

    /**
     * Dispatch an event to all reporters that accept it.
     * @param event Event to record.
     */
    public void recordEvent(final RecordableEvent event) {
        eventReporters.stream()
                .filter(reporter -> passesFilters(reporter, event))
                .forEach(reporter -> reporter.reportEvent(event));
    }

    public void addEventReporter(final BpEventReporter reporter) {
        eventReporters.add(reporter);
    }

    public void removeEventReporter(final BpEventReporter reporter) {
        eventReporters.remove(reporter);
    }

    private static Boolean passesFilters(final BpEventReporter reporter, final RecordableEvent event) {
        return reporter.getFilters()
                .stream()
                .allMatch(filter -> filter.shouldReportEvent(event));
    }

}
