package com.blispay.common.metrics.model.call;

import com.blispay.common.metrics.model.TrackingInfo;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TransactionData<R extends Resource, A extends Action> {

    @JsonProperty("direction")
    private final Direction direction;

    @JsonProperty("durationMillis")
    private final Long durationMillis;

    @JsonProperty("action")
    private final A action;

    @JsonProperty("resource")
    private final R resource;

    @JsonProperty("status")
    private final Integer status;

    @JsonProperty("msg")
    private final String message;

    @JsonProperty("trackingInfo")
    private TrackingInfo trackingInfo;

    private Map<String, Object> custom;

    public TransactionData(final Direction direction, final Long durationMillis,
                           final R resource, final A action, final Status status,
                           final String message, final TrackingInfo trackingInfo) {

        this(direction, durationMillis, resource, action, status, message, trackingInfo, new HashMap<>());
    }

    /**
     * Immutable base resource call event data.
     *
     * @param direction Direction of resource call.
     * @param durationMillis Time in milliseconds for call to complete.
     * @param resource Resource being called.
     * @param action Action of call.
     * @param status Status of the response.
     * @param message Optional message.
     * @param trackingInfo Tracking info for context.
     * @param custom Map of custom key value pairs to append to the object.
     */
    public TransactionData(final Direction direction, final Long durationMillis,
                           final R resource, final A action, final Status status,
                           final String message, final TrackingInfo trackingInfo,
                           final Map<String, Object> custom) {

        this.direction = direction;
        this.durationMillis = durationMillis;
        this.action = action;
        this.resource = resource;
        this.status = status.getValue();
        this.message = message;
        this.trackingInfo = trackingInfo;
        this.custom = Collections.unmodifiableMap(custom);
    }

    public Direction getDirection() {
        return direction;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public A getAction() {
        return action;
    }

    public R getResource() {
        return resource;
    }

    public Integer getStatus() {
        return status;
    }

    public TrackingInfo getTrackingInfo() {
        return trackingInfo;
    }

    public String getMessage() {
        return message;
    }

    @JsonAnyGetter
    public Map<String, Object> getCustom() {
        return custom;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Direction direction;
        private Long durationMillis;
        private Action action;
        private Resource resource;
        private Status status;
        private String message;
        private TrackingInfo trackingInfo;
        private Map<String, Object> customData = new HashMap<>();

        public Builder() {

        }

        public Builder direction(final Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder duration(final Duration duration) {
            this.durationMillis = duration.toMillis();
            return this;
        }

        public Builder action(final Action action) {
            this.action = action;
            return this;
        }

        public Builder resource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public Builder trackingInfo(final TrackingInfo trackingInfo) {
            this.trackingInfo = trackingInfo;
            return this;
        }

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Builder custom(final Map<String, Object> customData) {
            this.customData.putAll(customData);
            return this;
        }

        public Builder customField(final String key, final Object value) {
            this.customData.put(key, value);
            return this;
        }

        public TransactionData build() {
            return new TransactionData<>(direction, durationMillis, resource, action, status, message, trackingInfo, customData);
        }
    }

}
