package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ResourceUtilizationDataMatcher extends TypeSafeMatcher<ResourceUtilizationData> {

    private final Matcher<Long> min;
    private final Matcher<Long> max;
    private final Matcher<Long> curr;
    private final Matcher<Double> currPct;

    /**
     * Resource utilization metric matcher.
     * @param min min utilization
     * @param max max utilization
     * @param curr current utilization
     * @param currPct current percent of max utilized.
     */
    public ResourceUtilizationDataMatcher(final Long min, final Long max, final Long curr, final Double currPct) {
        this.min = Matchers.equalTo(min);
        this.max = Matchers.equalTo(max);
        this.curr = Matchers.equalTo(curr);
        this.currPct = Matchers.equalTo(currPct);
    }

    /**
     * Resource utilization metric matcher.
     * @param min min utilization
     * @param max max utilization
     * @param curr current utilization
     * @param currPct current percent of max utilized.
     */
    public ResourceUtilizationDataMatcher(final Matcher<Long> min, final Matcher<Long> max, final Matcher<Long> curr, final Matcher<Double> currPct) {
        this.min = min;
        this.max = max;
        this.curr = curr;
        this.currPct = currPct;
    }


    @Override
    public boolean matchesSafely(final ResourceUtilizationData resourceUtilizationMetric) {
        return min.matches(resourceUtilizationMetric.getMinValue())
                && max.matches(resourceUtilizationMetric.getMaxValue())
                && curr.matches(resourceUtilizationMetric.getCurrentValue())
                && currPct.matches(resourceUtilizationMetric.getCurrentPercentage());
    }

    @Override
    public void describeTo(final Description description) {

    }
}
