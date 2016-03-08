package com.blispay.common.metrics;

import com.blispay.common.metrics.matchers.TrackingInfoMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.TrackingInfoAware;
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

    protected Boolean approximatelyEqual(final Double expected, final Double actual, final Double acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

    protected static PiiBusinessEventData defaultPiiBusinessEventData() {
        return defaultPiiBusinessEventData("user1");
    }

    protected static PiiBusinessEventData defaultPiiBusinessEventData(final String username) {
        return new PiiBusinessEventData(username, "Some notes", 1);
    }

    protected static PiiBusinessEventDataMatcher defaultPiiDataMatcher(final String username, final TrackingInfo trackingInfo) {
        return new PiiBusinessEventDataMatcher(username, "Some notes", 1, trackingInfo);
    }

    protected static TrackingInfo trackingInfo() {
        final TrackingInfo ti = new TrackingInfo(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        LocalMetricContext.setTrackingInfo(ti.getUserTrackingId(), ti.getAgentTrackingId(), ti.getSessionTrackingId(), ti.getApiTrackingId());

        return ti;
    }

    protected static EventModel<PiiBusinessEventData> testEvent() {
        final EventRepository<PiiBusinessEventData> repo = MetricService.globalInstance().eventRepository(PiiBusinessEventData.class)
                .ofType(EventType.BUSINESS_EVT)
                .inGroup(EventGroup.ACCOUNT_DOMAIN)
                .withName("some-event")
                .build();

        return repo.save(defaultPiiBusinessEventData());
    }

    protected static class PiiBusinessEventData implements TrackingInfoAware {

        @JsonProperty("user_name")
        private final String userName;

        @JsonProperty("notes")
        private final String notes;

        @JsonProperty("count")
        private final Integer count;

        @JsonProperty("trackingInfo")
        private TrackingInfo trackingInfo;

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

        public TrackingInfo getTrackingInfo() {
            return trackingInfo;
        }

        @Override
        public void setTrackingInfo(final TrackingInfo trackingInfo) {
            this.trackingInfo = trackingInfo;
        }
    }

    protected static class PiiBusinessEventDataMatcher extends TypeSafeMatcher<PiiBusinessEventData> {

        private final Matcher<String> usernameMatcher;
        private final Matcher<String> notesMatcher;
        private final Matcher<Integer> countMatcher;
        private final Matcher<TrackingInfo> trackingInfoMatcher;

        public PiiBusinessEventDataMatcher(final String username, final String notes, final Integer count,
                                           final TrackingInfo trackingInfo) {

            this.usernameMatcher = Matchers.equalTo(username);
            this.notesMatcher = Matchers.equalTo(notes);
            this.countMatcher = Matchers.equalTo(count);
            this.trackingInfoMatcher = new TrackingInfoMatcher(trackingInfo);
        }

        @Override
        public boolean matchesSafely(final PiiBusinessEventData piiBusinessEventData) {
            return usernameMatcher.matches(piiBusinessEventData.getUserName())
                    && notesMatcher.matches(piiBusinessEventData.getNotes())
                    && countMatcher.matches(piiBusinessEventData.getCount())
                    && trackingInfoMatcher.matches(piiBusinessEventData.getTrackingInfo());
        }

        @Override
        public void describeTo(final Description description) {

        }
    }

}
