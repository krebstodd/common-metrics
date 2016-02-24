package com.blispay.common.metrics.model.business;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;

import java.time.ZonedDateTime;

public class EventMetric<T> extends BaseMetricModel<T> {

    private static final MetricType type = MetricType.EVENT;

    private final T eventData;

    /**
     * Immutable event occurrence metric.
     *
     * @param timestamp timestamp for when the metric occurred.
     * @param applicationId application name.
     * @param group metric group.
     * @param name metric name.
     * @param eventData summary of the event.
     */
    public EventMetric(final ZonedDateTime timestamp,
                       final String applicationId,
                       final MetricGroup group,
                       final String name,
                       final T eventData) {

        super(timestamp, applicationId, group, name, type);

        this.eventData = eventData;
    }


    @Override
    public T eventData() {
        return eventData;
    }
}
