package com.blispay.common.metrics.report;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.MetricSerializer;
import com.blispay.common.metrics.model.BaseMetricModel;
import org.slf4j.Logger;

public class Slf4jEventReporter extends EventReporter {

    private final MetricSerializer metricSerializer;
    private final Logger logger;

    public Slf4jEventReporter(final Logger eventLogger) {
        this(new JsonMetricSerializer(), eventLogger);
    }

    public Slf4jEventReporter(final MetricSerializer metricSerializer, final Logger eventLogger) {
        this.metricSerializer = metricSerializer;
        this.logger = eventLogger;
    }

    @Override
    public void acceptEvent(final BaseMetricModel event) {
        logger.info(metricSerializer.serialize(event));
    }

}
