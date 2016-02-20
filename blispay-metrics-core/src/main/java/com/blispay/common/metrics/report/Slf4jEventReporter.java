package com.blispay.common.metrics.report;

import com.blispay.common.metrics.model.BaseMetricModel;
import org.slf4j.Logger;

public class Slf4jEventReporter extends EventReporter {

    user mapper to secure jsonify

    private final Logger logger;

    public Slf4jEventReporter(final Logger eventLogger) {
        this.logger = eventLogger;
    }

    @Override
    public void acceptEvent(final BaseMetricModel event) {
        logger.info(event.toString());
    }

}
