package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrackingInfo {

    @JsonProperty("agentTrackingId")
    private final String agentTrackingId;

    @JsonProperty("userTrackingId")
    private final String userTrackingId;

    @JsonProperty("apiTrackingId")
    private final String apiTrackingId;

    @JsonProperty("sessionTrackingId")
    private final String sessionTrackingId;

    /**
     * Tracking info for a request.
     *
     * @param userTrackingId user tracking id.
     * @param agentTrackingId agent tracking id.
     * @param sessionTrackingId session tracking id.
     * @param apiTrackingId api tracking id.
     */
    public TrackingInfo(final String userTrackingId, final String agentTrackingId,
                        final String sessionTrackingId, final String apiTrackingId) {

        this.agentTrackingId = agentTrackingId;
        this.userTrackingId = userTrackingId;
        this.apiTrackingId = apiTrackingId;
        this.sessionTrackingId = sessionTrackingId;
    }

    public String getAgentTrackingId() {
        return agentTrackingId;
    }

    public String getUserTrackingId() {
        return userTrackingId;
    }

    public String getApiTrackingId() {
        return apiTrackingId;
    }

    public String getSessionTrackingId() {
        return sessionTrackingId;
    }
}
