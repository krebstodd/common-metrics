package com.blispay.common.metrics.model.status;

public class StatusData {

    private final Boolean statusValue;

    private final String statusMessage;

    /**
     * The object under monitoring is currenlty unhealthy with a message and corresponding exception.
     *
     * @param statusValue The object is healthy.
     * @param statusMessage The message to display.
     */
    public StatusData(final boolean statusValue, final String statusMessage) {
        this.statusValue = statusValue;
        this.statusMessage = statusMessage;
    }

    public Boolean getStatusValue() {
        return statusValue;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

}
