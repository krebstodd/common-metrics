package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ResourceCallMetricMatcher<T> extends TypeSafeMatcher<BaseResourceCallMetric<T>> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<T> dataMatcher;

    public ResourceCallMetricMatcher(final MetricGroup group, final String name,
                                     final MetricType mType, final Matcher<T> eventDataMatcher) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(mType.getValue());

        this.dataMatcher = eventDataMatcher;

    }

    @Override
    public boolean matchesSafely(final BaseResourceCallMetric<T> tBaseResourceCallMetric) {
        return timestampMatcher.matches(tBaseResourceCallMetric.getTimestamp())
                && groupMatcher.matches(tBaseResourceCallMetric.getGroup().getValue())
                && nameMatcher.matches(tBaseResourceCallMetric.getName())
                && typeMatcher.matches(tBaseResourceCallMetric.getType().getValue())
                && dataMatcher.matches(tBaseResourceCallMetric.eventData());
    }

    @Override
    public void describeTo(final Description description) {

    }
}
