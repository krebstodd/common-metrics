package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetric;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ResourceUtilizationMetricMatcher extends TypeSafeMatcher<ResourceUtilizationMetric> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<Long> min;
    private final Matcher<Long> max;
    private final Matcher<Long> curr;
    private final Matcher<Double> currPct;

    /**
     * Resource utilization metric matcher.
     * @param group group
     * @param name name
     * @param type type
     * @param min min utilization
     * @param max max utilization
     * @param curr current utilization
     * @param currPct current percent of max utilized.
     */
    public ResourceUtilizationMetricMatcher(final MetricGroup group, final String name, final MetricType type,
                                            final Long min, final Long max, final Long curr, final Double currPct) {

        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(type.getValue());

        this.min = Matchers.equalTo(min);
        this.max = Matchers.equalTo(max);
        this.curr = Matchers.equalTo(curr);
        this.currPct = Matchers.equalTo(currPct);

    }

    @Override
    public boolean matchesSafely(final ResourceUtilizationMetric resourceUtilizationMetric) {
        return timestampMatcher.matches(resourceUtilizationMetric.getTimestamp())
                && groupMatcher.matches(resourceUtilizationMetric.getGroup().getValue())
                && nameMatcher.matches(resourceUtilizationMetric.getName())
                && typeMatcher.matches(resourceUtilizationMetric.getType().getValue())
                && min.matches(resourceUtilizationMetric.eventData().getMinValue())
                && max.matches(resourceUtilizationMetric.eventData().getMaxValue())
                && curr.matches(resourceUtilizationMetric.eventData().getCurrentValue())
                && currPct.matches(resourceUtilizationMetric.eventData().getCurrentPercentage());
    }

    @Override
    public void describeTo(final Description description) {

    }
}
