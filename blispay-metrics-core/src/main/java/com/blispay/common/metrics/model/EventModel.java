package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventModel<D> {

    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonProperty("application")
    private final String application;

    @JsonProperty("group")
    private final EventGroup group;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("type")
    private final EventType type;

    @JsonProperty("eventData")
    private final D eventData;

    /**
     * Immutable event model.
     *
     * @param timestamp timestamp for when the event occurred.
     * @param application application name.
     * @param group hierarchical event group, helps with searching. 
     * @param name event name.
     * @param type event type, dictates the structure of the event eventData payload.
     * @param eventData Payload of event.             
     */
    public EventModel(final ZonedDateTime timestamp,
                      final String application,
                      final EventGroup group,
                      final String name,
                      final EventType type,
                      final D eventData) {

        this.timestamp = dtFormatter.format(timestamp.withZoneSameInstant(ZoneId.of("UTC")));
        this.application = application;
        this.group = group;
        this.name = name;
        this.type = type;
        this.eventData = eventData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public EventGroup getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public EventType getType() {
        return type;
    }

    public String getApplication() {
        return application;
    }

    public D eventData() {
        return eventData;
    }


}
