package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseMetricModel<D> {

    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonProperty("application")
    private final String application;

    @JsonProperty("group")
    private final MetricGroup group;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("type")
    private final MetricType type;

    /**
     * Immutable metric model.
     *
     * @param timestamp timestamp for when the metric occurred.
     * @param application application name.
     * @param group hierarchical metric group, helps with searching. .
     * @param name metric name.
     * @param type metric type, dictates the structure of the metric eventData payload.
     */
    public BaseMetricModel(final ZonedDateTime timestamp,
                           final String application,
                           final MetricGroup group,
                           final String name,
                           final MetricType type) {

        this.timestamp = dtFormatter.format(timestamp.withZoneSameInstant(ZoneId.of("UTC")));
        this.application = application;
        this.group = group;
        this.name = name;
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public MetricGroup getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public MetricType getType() {
        return type;
    }

    public String getApplication() {
        return application;
    }

    @JsonProperty("eventData")
    public abstract D eventData();

}
