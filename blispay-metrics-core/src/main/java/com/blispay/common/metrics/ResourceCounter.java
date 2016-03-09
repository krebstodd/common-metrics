package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.counter.ResourceCountData;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NameFormatter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class ResourceCounter {

    private final String applicationId;
    private final EventEmitter emitter;
    private final EventGroup group;
    private final String name;

    private ResourceCounter(final String applicationId, final EventEmitter emitter,
                            final EventGroup group, final String name) {

        this.applicationId = applicationId;
        this.emitter = emitter;
        this.group = group;
        this.name = name;
    }

    public void updateCount(final Double count) {
        updateCount(ZonedDateTime.now(ZoneId.of("UTC")), count, null, LocalMetricContext.getTrackingInfo());
    }

    public void updateCount(final ZonedDateTime timestamp,
                            final Double count,
                            final Object userData,
                            final TrackingInfo trackingInfo) {

        emitter.emit(new EventModel<>(createHeader(timestamp, trackingInfo), new ResourceCountData(count), userData));
    }

    private EventHeader createHeader(final ZonedDateTime timestamp, final TrackingInfo trackingInfo) {
        return EventHeader.builder()
                .timestamp(timestamp)
                .applicationId(applicationId)
                .group(group)
                .type(EventType.RESOURCE_COUNT)
                .trackingInfo(trackingInfo)
                .name(name)
                .build();
    }

    public static class Builder {

        private final String applicationId;
        private final EventEmitter emitter;

        private EventGroup group;
        private String name;

        public Builder(final String applicationId, final EventEmitter emitter) {
            this.applicationId = applicationId;
            this.emitter = emitter;
        }

        public Builder inGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withNameFromType(final Class<?> type) {
            this.name = NameFormatter.toEventName(type);
            return this;
        }

        public ResourceCounter build() {
            return new ResourceCounter(applicationId, emitter, group, name);
        }
    }

}
