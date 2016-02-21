package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONObject;

public class JsonMetricMatcher extends TypeSafeMatcher<JSONObject> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher userTrackingInfoMatcher;
    private final Matcher eventDataMatcher;

    public JsonMetricMatcher(final MetricGroup group, final String name, final MetricType type,
                             final Matcher eventDataMatcher) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(type.getValue());
        this.userTrackingInfoMatcher = new UserTrackingInfoMatcher();
        this.eventDataMatcher = eventDataMatcher;

    }

    @Override
    protected boolean matchesSafely(final JSONObject jsonObject) {
        
        return timestampMatcher.matches(JsonMetricUtil.parseTimeStamp(jsonObject))
                && groupMatcher.matches(JsonMetricUtil.parseGroup(jsonObject))
                && nameMatcher.matches(JsonMetricUtil.parseName(jsonObject))
                && typeMatcher.matches(JsonMetricUtil.parseType(jsonObject))
                && userTrackingInfoMatcher.matches(JsonMetricUtil.parseTrackingInfo(jsonObject))
                && eventDataMatcher.matches(JsonMetricUtil.parseEventData(jsonObject));
    }

    @Override
    public void describeTo(final Description description) {

    }
}
