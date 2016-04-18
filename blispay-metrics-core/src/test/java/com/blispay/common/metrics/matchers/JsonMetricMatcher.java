package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONObject;

public class JsonMetricMatcher extends TypeSafeMatcher<JSONObject> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> applicationIdMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher dataMatcher;
    private final Matcher userDataMatcher;

    /**
     * Match a json metric.
     * @param group group
     * @param applicationName app name
     * @param name name
     * @param type type
     * @param dataMatcher data
     * @param userDataMatcher user data
     */
    public JsonMetricMatcher(final EventGroup group, final String applicationName, final String name, final EventType type,
                             final Matcher dataMatcher, final Matcher userDataMatcher) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.applicationIdMatcher = Matchers.equalTo(applicationName);
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(type.getValue());
        this.dataMatcher = dataMatcher;
        this.userDataMatcher = userDataMatcher;

    }

    @Override
    public boolean matchesSafely(final JSONObject jsonObject) {

        System.out.println(timestampMatcher.matches(JsonMetricUtil.parseTimeStamp(jsonObject)));
        System.out.println(applicationIdMatcher.matches(JsonMetricUtil.parseApplication(jsonObject)));
        System.out.println(groupMatcher.matches(JsonMetricUtil.parseGroup(jsonObject)));
        System.out.println(nameMatcher.matches(JsonMetricUtil.parseName(jsonObject)));
        System.out.println(typeMatcher.matches(JsonMetricUtil.parseType(jsonObject)));
        System.out.println(dataMatcher.matches(JsonMetricUtil.parseData(jsonObject)));
        
        return timestampMatcher.matches(JsonMetricUtil.parseTimeStamp(jsonObject))
                && applicationIdMatcher.matches(JsonMetricUtil.parseApplication(jsonObject))
                && groupMatcher.matches(JsonMetricUtil.parseGroup(jsonObject))
                && nameMatcher.matches(JsonMetricUtil.parseName(jsonObject))
                && typeMatcher.matches(JsonMetricUtil.parseType(jsonObject))
                && dataMatcher.matches(JsonMetricUtil.parseData(jsonObject))
                && userDataMatcher.matches(JsonMetricUtil.parseUserData(jsonObject));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("timestamp=[")
                .appendDescriptionOf(timestampMatcher)
                .appendText("], applicationId=[")
                .appendDescriptionOf(applicationIdMatcher)
                .appendText("], group=[")
                .appendDescriptionOf(groupMatcher)
                .appendText("], name=[")
                .appendDescriptionOf(nameMatcher)
                .appendText("], type=[")
                .appendDescriptionOf(typeMatcher)
                .appendText("], data=[")
                .appendDescriptionOf(dataMatcher)
                .appendText("], userData=[")
                .appendDescriptionOf(userDataMatcher)
                .appendText("]");
    }
}
