package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class TransactionDataMatcher extends TypeSafeMatcher<TransactionData> {

    private final Matcher<String> resourceMatcher;
    private final Matcher<String> actionMatcher;
    private final Matcher<Integer> statusMatcher;
    private final Matcher<Direction> directionMatcher;
    private final Long approxRuntime;
    private final Long acceptableDelta;

    /**
     * Match a resource call metric.
     * @param resource resource
     * @param action action
     * @param direction direction
     * @param status status
     * @param approxMillis approxMillis
     */
    public TransactionDataMatcher(final Resource resource, final Action action, final Direction direction,
                                  final Status status, final Long approxMillis) {

        this(resource, action, direction, status, approxMillis, approxMillis);
    }

    /**
     * Match a resource call metric.
     * @param resource resource
     * @param action action
     * @param direction direction
     * @param status status
     * @param approxMillis approxMillis
     * @param acceptableDelta acceptableDelta
     */
    public TransactionDataMatcher(final Resource resource, final Action action, final Direction direction,
                                  final Status status, final Long approxMillis, final Long acceptableDelta) {
        
        this.resourceMatcher = Matchers.equalTo(resource.getValue());
        this.actionMatcher = Matchers.equalTo(action.getValue());
        this.directionMatcher = Matchers.equalTo(direction);
        this.statusMatcher = Matchers.equalTo(status.getValue());
        this.approxRuntime = approxMillis;
        this.acceptableDelta = acceptableDelta;
    }

    @Override
    public boolean matchesSafely(final TransactionData raBaseResourceCallEventData) {
        return resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue())
                && actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue())
                && statusMatcher.matches(raBaseResourceCallEventData.getStatus())
                && directionMatcher.matches(raBaseResourceCallEventData.getDirection())
                && approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), acceptableDelta);
    }

    @Override
    public void describeTo(final Description description) {

    }

    protected Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}