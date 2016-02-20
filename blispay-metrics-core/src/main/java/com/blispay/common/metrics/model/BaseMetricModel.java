package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class BaseMetricModel<D> {

    @JsonProperty("timestamp")
    private final ZonedDateTime timestamp;

    @JsonProperty("group")
    private final String group;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("type")
    private final Type type;

    public BaseMetricModel(final ZonedDateTime timestamp,
                           final MetricGroup group,
                           final String name,
                           final Type type) {

        this.timestamp = timestamp;
        this.group = group.toString();
        this.name = name;
        this.type = type;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @JsonProperty("eventData")
    public abstract D eventData();

    public enum Type {

        RESOURCE_CALL("CAL"),
        RESOURCE_UTILIZATION("UTL"),
        RESOURCE_COUNTER("CNT"),
        EVENT("EVENT");

        private final String type;

        private Type(final String type) {
            this.type = type;
        }

        @JsonValue
        public String getType() {
            return type;
        }

    }

}
