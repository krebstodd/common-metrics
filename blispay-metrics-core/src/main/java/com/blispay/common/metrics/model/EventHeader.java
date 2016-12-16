package com.blispay.common.metrics.model;

import com.blispay.common.metrics.util.NameFormatter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class EventHeader.
 */
public final class EventHeader {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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
    public EventHeader(final ZonedDateTime timestamp, final String application, final EventGroup group, final EventType type, final String name, final TrackingInfo trackingInfo) {

        this.timestamp = DT_FORMATTER.format(timestamp);
        this.application = application;
        this.group = group;
        this.type = type;
        this.name = name;
        this.trackingInfo = trackingInfo;
    }

    /**
     * Method getTimestamp.
     *
     * @return return value.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Method getApplication.
     *
     * @return return value.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Method getGroup.
     *
     * @return return value.
     */
    public EventGroup getGroup() {
        return group;
    }

    /**
     * Method getType.
     *
     * @return return value.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Method getName.
     *
     * @return return value.
     */
    public String getName() {
        return name;
    }

    /**
     * Method getTrackingInfo.
     *
     * @return return value.
     */
    public TrackingInfo getTrackingInfo() {
        return trackingInfo;
    }

    /**
     * Method builder.
     *
     * @return return value.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Class Builder.
     */
    public static class Builder {

        private ZonedDateTime timestamp;
        private String applicationId;
        private EventGroup group;
        private EventType type;
        private String name;
        private TrackingInfo trackingInfo;

        /**
         * Method timestamp.
         *
         * @param timestamp timestamp.
         * @return return value.
         */
        public Builder timestamp(final ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Method applicationId.
         *
         * @param applicationId applicationId.
         * @return return value.
         */
        public Builder applicationId(final String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        /**
         * Method group.
         *
         * @param group group.
         * @return return value.
         */
        public Builder group(final EventGroup group) {
            this.group = group;
            return this;
        }

        /**
         * Method type.
         *
         * @param type type.
         * @return return value.
         */
        public Builder type(final EventType type) {
            this.type = type;
            return this;
        }

        /**
         * Method name.
         *
         * @param name name.
         * @return return value.
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Method nameFromType.
         *
         * @param type type.
         * @return return value.
         */
        public Builder nameFromType(final Class<?> type) {
            this.name = NameFormatter.toEventName(type);
            return this;
        }

        /**
         * Method trackingInfo.
         *
         * @param trackingInfo trackingInfo.
         * @return return value.
         */
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
