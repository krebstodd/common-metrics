package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class EventMatcher<T> extends TypeSafeMatcher<EventModel<T>> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> applicationMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<T> dataMatcher;

    /**
     * Resource call matcher.
     * @param applicationId application.
     * @param group group
     * @param name name
     * @param metricType type
     * @param eventDataMatcher data matcher
     */
    public EventMatcher(final String applicationId,
                        final EventGroup group, final String name,
                        final EventType metricType, final Matcher<T> eventDataMatcher) {

        this.applicationMatcher = Matchers.equalTo(applicationId);
        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(metricType.getValue());

        this.dataMatcher = eventDataMatcher;

    }

    @Override
    public boolean matchesSafely(final EventModel<T> metric) {
        System.out.println("========== START EVENT MATCHER FOR " + metric.getName());
        System.out.println(timestampMatcher.matches(metric.getTimestamp()));
        System.out.println(applicationMatcher.matches(metric.getApplication()));
        System.out.println(groupMatcher.matches(metric.getGroup().getValue()));
        System.out.println(nameMatcher.matches(metric.getName()));
        System.out.println(typeMatcher.matches(metric.getType().getValue()));
        System.out.println(dataMatcher.matches(metric.eventData()));
        System.out.println("========== END EVENT MATCHER FOR " + metric.getName());

        return timestampMatcher.matches(metric.getTimestamp())
                && applicationMatcher.matches(metric.getApplication())
                && groupMatcher.matches(metric.getGroup().getValue())
                && nameMatcher.matches(metric.getName())
                && typeMatcher.matches(metric.getType().getValue())
                && dataMatcher.matches(metric.eventData());
    }

    @Override
    public void describeTo(final Description description) {

    }

}

