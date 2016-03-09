package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class EventHeader {

    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonProperty("application")
    private final String application;

    @JsonProperty("group")
    private final EventGroup group;

    @JsonProperty("type")
    private final EventType type;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("trackingInfo")
    private final TrackingInfo trackingInfo;

    /**
     * Create a new event header instance.
     *
     * @param timestamp Timestamp.
     * @param application Application id.
     * @param group Event Group.
     * @param type Event type.
     * @param name Event name.
     * @param trackingInfo Event tracking info.
     */
    public EventHeader(final ZonedDateTime timestamp,
                       final String application,
                       final EventGroup group,
                       final EventType type,
                       final String name,
                       final TrackingInfo trackingInfo) {

        this.timestamp = dtFormatter.format(timestamp);
        this.application = application;
        this.group = group;
        this.type = type;
        this.name = name;
        this.trackingInfo = trackingInfo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getApplication() {
        return application;
    }

    public EventGroup getGroup() {
        return group;
    }

    public EventType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public TrackingInfo getTrackingInfo() {
        return trackingInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ZonedDateTime timestamp;
        private String applicationId;
        private EventGroup group;
        private EventType type;
        private String name;
        private TrackingInfo trackingInfo;

        public Builder timestamp(final ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder applicationId(final String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder group(final EventGroup group) {
            this.group = group;
            return this;
        }

        public Builder type(final EventType type) {
            this.type = type;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder trackingInfo(final TrackingInfo trackingInfo) {
            this.trackingInfo = trackingInfo;
            return this;
        }

        /**
         * Build a new event header.
         * @return event header.
         */
        public EventHeader build() {

            Preconditions.checkNotNull(timestamp);
            Preconditions.checkNotNull(applicationId);
            Preconditions.checkNotNull(group);
            Preconditions.checkNotNull(type);

            return new EventHeader(timestamp, applicationId, group, type, name, trackingInfo);

        }
    }
}
