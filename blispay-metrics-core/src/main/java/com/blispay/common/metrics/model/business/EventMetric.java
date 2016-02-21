package com.blispay.common.metrics.model.business;

import com.blispay.common.metrics.model.BaseUserTrackingMetric;
import com.blispay.common.metrics.model.BusinessEventName;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.UserTrackingInfo;

import java.time.ZonedDateTime;

public class EventMetric<T> extends BaseUserTrackingMetric<T> {

    private static final MetricType type = MetricType.EVENT;

    private final T eventData;

    public EventMetric(final ZonedDateTime timestamp,
                       final MetricGroup group,
                       final String name,
                       final UserTrackingInfo trackingInfo,
                       final T eventData) {

        super(timestamp, group, name, type);

        setTrackingInfo(trackingInfo);
        this.eventData = eventData;
    }


    @Override
    public T eventData() {
        return eventData;
    }
}
