package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EventModel<D, U> {

    @JsonUnwrapped
    private final EventHeader header;

    @JsonProperty("data")
    private final D data;

    @JsonProperty("userData")
    private final U userData;

    public EventModel(final EventHeader header,
                      final D data) {

        this(header, data, null);
    }

    /**
     * Immutable event model.
     *
     * @param header Event header.
     * @param data Payload of event.
     * @param userData Custom user data.
     */
    public EventModel(final EventHeader header,
                      final D data,
                      final U userData) {

        this.header = header;
        this.data = data;
        this.userData = userData;
    }

    public D getData() {
        return data;
    }

    public U getUserData() {
        return userData;
    }

    public EventHeader getHeader() {
        return this.header;
    }

}
