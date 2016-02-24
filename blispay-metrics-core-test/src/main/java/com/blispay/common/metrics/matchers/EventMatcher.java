package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.business.EventMetric;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class EventMatcher<T> extends TypeSafeMatcher<EventMetric<T>> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<T> dataMatcher;

    /**
     * Business event matcher.
     * @param group group
     * @param name name
     * @param type type
     * @param eventDataMatcher data
     */
    public EventMatcher(final MetricGroup group, final String name,
                        final MetricType type, final Matcher<T> eventDataMatcher) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(type.getValue());
        this.dataMatcher = eventDataMatcher;

    }

    @Override
    public boolean matchesSafely(final EventMetric<T> metric) {
        return timestampMatcher.matches(metric.getTimestamp())
                && groupMatcher.matches(metric.getGroup().getValue())
                && nameMatcher.matches(metric.getName())
                && typeMatcher.matches(metric.getType().getValue())
                && dataMatcher.matches(metric.eventData());
    }

    @Override
    public void describeTo(final Description description) {

    }
}
