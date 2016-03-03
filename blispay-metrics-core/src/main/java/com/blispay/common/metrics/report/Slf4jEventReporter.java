package com.blispay.common.metrics.report;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.EventSerializer;
import com.blispay.common.metrics.model.EventModel;
import org.slf4j.Logger;

public class Slf4jEventReporter extends EventReporter {

    private final EventSerializer metricSerializer;
    private final Logger logger;

    public Slf4jEventReporter(final Logger eventLogger) {
        this(new JsonMetricSerializer(), eventLogger);
    }

    public Slf4jEventReporter(final EventSerializer metricSerializer, final Logger eventLogger) {
        this.metricSerializer = metricSerializer;
        this.logger = eventLogger;
    }

    @Override
    public void acceptEvent(final EventModel event) {
        logger.info(metricSerializer.serialize(event));
    }

}
