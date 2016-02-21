package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTrackingInfo {

    @JsonProperty("agentTrackingId")
    private final String agentTrackingId;

    @JsonProperty("userTrackingId")
    private final String userTrackingId;

    @JsonProperty("apiTrackingId")
    private final String apiTrackingId;

    @JsonProperty("sessionTrackingId")
    private final String sessionTrackingId;

    public UserTrackingInfo(final String userTrackingId, final String agentTrackingId,
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
