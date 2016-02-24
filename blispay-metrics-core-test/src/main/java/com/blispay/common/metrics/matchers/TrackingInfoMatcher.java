package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.UserTrackingInfo;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class TrackingInfoMatcher extends TypeSafeMatcher<UserTrackingInfo> {

    private final Matcher<String> userIdMatcher;
    private final Matcher<String> sessionIdMatcher;
    private final Matcher<String> agentIdMatcher;
    private final Matcher<String> apiIdMatcher;

    public TrackingInfoMatcher(final UserTrackingInfo info) {
        this.userIdMatcher = Matchers.equalTo(info.getUserTrackingId());
        this.agentIdMatcher = Matchers.equalTo(info.getAgentTrackingId());
        this.apiIdMatcher = Matchers.equalTo(info.getApiTrackingId());
        this.sessionIdMatcher = Matchers.equalTo(info.getSessionTrackingId());
    }

    @Override
    public boolean matchesSafely(final UserTrackingInfo trackingInfo) {
        return userIdMatcher.matches(trackingInfo.getUserTrackingId())
                && sessionIdMatcher.matches(trackingInfo.getSessionTrackingId())
                && agentIdMatcher.matches(trackingInfo.getAgentTrackingId())
                && apiIdMatcher.matches(trackingInfo.getApiTrackingId());

    }

    @Override
    public void describeTo(final Description description) {

    }
}
