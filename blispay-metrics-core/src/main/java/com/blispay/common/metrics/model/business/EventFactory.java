package com.blispay.common.metrics.model.business;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.TrackingInfoAware;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventFactory<T> {

    private final String application;
    private final MetricGroup group;
    private final String name;

    /**
     * Event factory.
     * @param application The application name.
     * @param group Group for the particular event we're building.
     * @param name Name of the event.
     */
    public EventFactory(final String application, final MetricGroup group, final String name) {
        this.application = application;
        this.group = group;
        this.name = name;
    }

    public String getApplication() {
        return application;
    }

    public String getName() {
        return name;
    }

    public MetricGroup getGroup() {
        return group;
    }

    private static ZonedDateTime timestamp() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public EventMetric<T> newMetric(final T eventData) {

        if (eventData instanceof TrackingInfoAware) {
            ((TrackingInfoAware) eventData).setTrackingInfo(LocalMetricContext.getTrackingInfo());
        }

        return new EventMetric<>(timestamp(), getApplication(), getGroup(), getName(), eventData);

    }

}
