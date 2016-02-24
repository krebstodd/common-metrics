package com.blispay.common.metrics;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.business.EventMetric;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class AbstractMetricsTest {

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
        return defaultPiiBusinessEventData("user1");
    }

    protected static PiiBusinessEventData defaultPiiBusinessEventData(final String username) {
        return new PiiBusinessEventData(username, "Some notes", 1);
    }

    protected static PiiBusinessEventDataMatcher defaultPiiDataMatcher(final String username) {
        return new PiiBusinessEventDataMatcher(username, "Some notes", 1);
    }

    protected static UserTrackingInfo trackingInfo() {
        return new UserTrackingInfo(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    protected static EventMetric testEvent() {

        return new EventMetric<>(
                ZonedDateTime.now(),
                "someApp",
                MetricGroup.CLIENT,
                "created",
                defaultPiiBusinessEventData());

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

    protected static class PiiBusinessEventDataMatcher extends TypeSafeMatcher<PiiBusinessEventData> {

        private final Matcher<String> usernameMatcher;
        private final Matcher<String> notesMatcher;
        private final Matcher<Integer> countMatcher;

        public PiiBusinessEventDataMatcher(final String username, final String notes, final Integer count) {
            this.usernameMatcher = Matchers.equalTo(username);
            this.notesMatcher = Matchers.equalTo(notes);
            this.countMatcher = Matchers.equalTo(count);
        }

        @Override
        public boolean matchesSafely(final PiiBusinessEventData piiBusinessEventData) {
            return usernameMatcher.matches(piiBusinessEventData.getUserName())
                    && notesMatcher.matches(piiBusinessEventData.getNotes())
                    && countMatcher.matches(piiBusinessEventData.getCount());
        }

        @Override
        public void describeTo(final Description description) {

        }
    }

}
