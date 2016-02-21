package com.blispay.common.metrics.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONObject;

public class UserTrackingInfoMatcher extends TypeSafeMatcher<JSONObject> {

    @Override
    public boolean matchesSafely(final JSONObject jsonObject) {
        return jsonObject.getString("apiTrackingId") != null
                && jsonObject.getString("userTrackingId") != null
                && jsonObject.getString("agentTrackingId") != null
                && jsonObject.getString("sessionTrackingId") != null;
    }

    @Override
    public void describeTo(final Description description) {

    }
}
