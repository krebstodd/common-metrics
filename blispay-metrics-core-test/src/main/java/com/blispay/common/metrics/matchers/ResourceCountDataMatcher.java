package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.counter.ResourceCountData;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ResourceCountDataMatcher extends TypeSafeMatcher<ResourceCountData> {

    private final Matcher<Double> count;

    /**
     * Resource count metric matcher.
     * @param count count.
     */
    public ResourceCountDataMatcher(final Double count) {
        this.count = Matchers.equalTo(count);
    }

    /**
     * Resource count metric matcher.
     * @param count count.
     */
    public ResourceCountDataMatcher(final Matcher<Double> count) {
        this.count = count;
    }


    @Override
    public boolean matchesSafely(final ResourceCountData resourceUtilizationMetric) {
        return count.matches(resourceUtilizationMetric.getCount());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("count=[").appendDescriptionOf(count).appendText("]");
    }
}