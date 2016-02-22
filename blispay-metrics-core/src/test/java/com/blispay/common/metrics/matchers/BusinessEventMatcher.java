package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.business.EventMetric;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class BusinessEventMatcher<T> extends TypeSafeMatcher<EventMetric<T>> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<T> dataMatcher;

    public BusinessEventMatcher(final MetricGroup group, final String name,
                                final MetricType mType, final Matcher<T> eventDataMatcher) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(mType.getValue());
        this.dataMatcher = eventDataMatcher;

    }

    @Override
    protected boolean matchesSafely(final EventMetric<T> tEventMetric) {
        return timestampMatcher.matches(tEventMetric.getTimestamp())
                && groupMatcher.matches(tEventMetric.getGroup().getValue())
                && nameMatcher.matches(tEventMetric.getName())
                && typeMatcher.matches(tEventMetric.getType().getValue())
                && dataMatcher.matches(tEventMetric.eventData());
    }

    @Override
    public void describeTo(final Description description) {

    }
}
