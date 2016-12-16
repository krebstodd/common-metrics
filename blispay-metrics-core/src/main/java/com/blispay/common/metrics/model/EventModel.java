package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Class EventModel.
 *
 * @param <D> Generic param type.
 * @param <U> Generic param type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EventModel<D, U> {

    @JsonUnwrapped
    private final EventHeader header;

    @JsonProperty("data")
    private final D data;

    @JsonProperty("userData")
    private final U userData;

    /**
     * Constructs EventModel.
     *
     * @param header header.
     * @param data data.
     */
    public EventModel(final EventHeader header, final D data) {

        this(header, data, null);
    }

    /**
     * Immutable event model.
     *
     * @param header Event header.
     * @param data Payload of event.
     * @param userData Custom user data.
     */
    public EventModel(final EventHeader header, final D data, final U userData) {

        this.header = header;
        this.data = data;
        this.userData = userData;
    }

    /**
     * Method getData.
     *
     * @return return value.
     */
    public D getData() {
        return data;
    }

    /**
     * Method getUserData.
     *
     * @return return value.
     */
    public U getUserData() {
        return userData;
    }

    /**
     * Method getHeader.
     *
     * @return return value.
     */
    public EventHeader getHeader() {
        return this.header;
    }

}
