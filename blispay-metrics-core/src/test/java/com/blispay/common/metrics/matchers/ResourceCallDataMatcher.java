package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.BaseResourceCallEventData;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ResourceCallDataMatcher<R extends Resource, A extends Action> extends TypeSafeMatcher<BaseResourceCallEventData<R, A>> {

    private static Long ACCEPTABLE_RT_DELTA = 100L;

    private final Matcher<String> resourceMatcher;
    private final Matcher<String> actionMatcher;
    private final Matcher<Integer> statusMatcher;
    private final Matcher<Direction> directionMatcher;
    private final Matcher<TrackingInfo> trackingInfoMatcher;
    private final Long approxRuntime;

    /**
     * Match a resource call metric.
     * @param resource resource
     * @param action action
     * @param direction direction
     * @param status status
     * @param approxMillis approxMillis
     * @param trackingInfo trackinginfo
     */
    public ResourceCallDataMatcher(final R resource, final A action, final Direction direction,
                                   final Status status, final Long approxMillis, final TrackingInfo trackingInfo) {

        this.resourceMatcher = Matchers.equalTo(resource.getValue());
        this.actionMatcher = Matchers.equalTo(action.getValue());
        this.directionMatcher = Matchers.equalTo(direction);
        this.statusMatcher = Matchers.equalTo(status.getValue());
        this.trackingInfoMatcher = new TrackingInfoMatcher(trackingInfo);
        this.approxRuntime = approxMillis;
    }

    @Override
    public boolean matchesSafely(final BaseResourceCallEventData<R, A> raBaseResourceCallEventData) {
        return resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue())
                && actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue())
                && statusMatcher.matches(raBaseResourceCallEventData.getStatus())
                && directionMatcher.matches(raBaseResourceCallEventData.getDirection())
                && trackingInfoMatcher.matches(raBaseResourceCallEventData.getTrackingInfo())
                && approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), ACCEPTABLE_RT_DELTA);
    }

    @Override
    public void describeTo(final Description description) {

    }

    protected Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
