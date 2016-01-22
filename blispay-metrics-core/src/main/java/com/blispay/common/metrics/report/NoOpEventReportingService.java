package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.MetricEvent;

public class NoOpEventReportingService extends BpEventService {

    @Override
    public void acceptEvent(final MetricEvent event) {
        // NO_OP
    }

}
