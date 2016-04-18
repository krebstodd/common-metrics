package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.UUID;

public abstract class AbstractMetricsTest {

    static {
        MetricService.globalInstance("metrics-test").start();
    }

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

    protected static PiiBusinessEventData defaultPiiBusinessEventData() {
        return defaultPiiBusinessEventData("user1");
    }

    protected static PiiBusinessEventData defaultPiiBusinessEventData(final String username) {
        return new PiiBusinessEventData(username, "Some notes", 1);
    }

    protected static PiiBusinessEventDataMatcher defaultPiiDataMatcher(final String username) {
        return new PiiBusinessEventDataMatcher(username, "Some notes", 1);
    }

    protected static TrackingInfo createAndSetThreadLocalTrackingInfo() {
        final TrackingInfo ti = new TrackingInfo(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        LocalMetricContext.setTrackingInfo(ti.getUserTrackingId(), ti.getAgentTrackingId(), ti.getSessionTrackingId(), ti.getApiTrackingId());

        return ti;
    }

    protected static EventModel<Void, PiiBusinessEventData> testEvent() {
        final EventFactory<PiiBusinessEventData> factory = MetricService.globalInstance().eventFactory(PiiBusinessEventData.class)
                .inGroup(EventGroup.ACCOUNT_DOMAIN)
                .withName("some-event")
                .build();

        return factory.save(defaultPiiBusinessEventData());
    }

    protected static class PiiBusinessEventData {

        @JsonProperty("user_name")
        private final String userName;

        @JsonProperty("notes")
        private final String notes;

        @JsonProperty("count")
        private final Integer count;

        public PiiBusinessEventData(final String username, final String notes,
                                    final Integer count) {

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
