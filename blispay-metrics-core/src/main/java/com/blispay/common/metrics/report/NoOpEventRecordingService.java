package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.RecordableEvent;

public class NoOpEventRecordingService extends BpEventRecordingService {

    @Override
    public void recordEvent(final RecordableEvent event) {
        // NO_OP
    }

}
