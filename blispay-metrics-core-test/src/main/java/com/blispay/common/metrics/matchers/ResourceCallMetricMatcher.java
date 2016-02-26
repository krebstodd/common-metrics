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

    /**
     * Resource call matcher.
     * @param group group
     * @param name name
     * @param metricType type
     * @param eventDataMatcher data matcher
     */
    public ResourceCallMetricMatcher(final MetricGroup group, final String name,
                                     final MetricType metricType, final Matcher<T> eventDataMatcher) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(metricType.getValue());

        this.dataMatcher = eventDataMatcher;

    }

    @Override
    public boolean matchesSafely(final BaseResourceCallMetric<T> metric) {
        System.out.println(timestampMatcher.matches(metric.getTimestamp()));
                System.out.println(groupMatcher.matches(metric.getGroup().getValue()));
                System.out.println(nameMatcher.matches(metric.getName()));
                System.out.println(typeMatcher.matches(metric.getType().getValue()));
                System.out.println(dataMatcher.matches(metric.eventData()));
        
        return timestampMatcher.matches(metric.getTimestamp())
                && groupMatcher.matches(metric.getGroup().getValue())
                && nameMatcher.matches(metric.getName())
                && typeMatcher.matches(metric.getType().getValue())
                && dataMatcher.matches(metric.eventData());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("timeStamp=[")
                .appendDescriptionOf(timestampMatcher)
                .appendText("], group=[")
                .appendDescriptionOf(groupMatcher)
                .appendText("], name=[")
                .appendDescriptionOf(nameMatcher)
                .appendText("], type=[")
                .appendDescriptionOf(typeMatcher)
                .appendText("], data=")
                .appendDescriptionOf(dataMatcher)
                .appendText("]");
    }
}
