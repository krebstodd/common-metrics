package com.blispay.common.metrics;

import com.blispay.common.metrics.model.UserTrackingInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.UUID;

public abstract class AbstractMetricsTest {

    protected static final MetricService metricService = MetricService.globalInstance();

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

    protected Boolean approximatelyEqual(final Double expected, final Double actual, final Double acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

    protected static PiiBusinessEventData defaultPiiBusinessEventData() {
        return new PiiBusinessEventData("user1", "Some notes", 1);
    }

    protected static UserTrackingInfo trackingInfo() {
        return new UserTrackingInfo(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    protected static class PiiBusinessEventData {

        @JsonProperty("user_name")
        public final String userName;

        @JsonProperty("notes")
        public final String notes;

        @JsonProperty("count")
        public final Integer count;

        public PiiBusinessEventData(final String username, final String notes, final Integer count) {
            this.userName = username;
            this.notes = notes;
            this.count = count;
        }

        public String getUserName() {
            return userName;
        }

        public String getNotes() {
            return notes;
        }

        public Integer getCount() {
            return count;
        }
    }

}
