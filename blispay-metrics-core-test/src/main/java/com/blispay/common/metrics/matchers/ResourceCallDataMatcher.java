package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.UserTrackingInfo;
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
    private final Matcher trackingInfoMatcher;
    private final Long approxRuntime;

    public ResourceCallDataMatcher(final R resource, final A action, final Direction direction,
                                   final Status status, final Long approxMillis, final UserTrackingInfo trackingInfo) {

        this.resourceMatcher = Matchers.equalTo(resource.getValue());
        this.actionMatcher = Matchers.equalTo(action.getValue());
        this.directionMatcher = Matchers.equalTo(direction);
        this.statusMatcher = Matchers.equalTo(status.getValue());

        if (trackingInfo == null) {
            trackingInfoMatcher = Matchers.nullValue();
        } else {
            this.trackingInfoMatcher = new TrackingInfoMatcher(trackingInfo);
        }

        this.approxRuntime = approxMillis;
    }

    @Override
    public boolean matchesSafely(final BaseResourceCallEventData<R, A> raBaseResourceCallEventData) {
        System.out.println(resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue()));
                System.out.println(actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue()));
                System.out.println(statusMatcher.matches(raBaseResourceCallEventData.getStatus()));
                System.out.println(directionMatcher.matches(raBaseResourceCallEventData.getDirection()));
                System.out.println(trackingInfoMatcher.matches(raBaseResourceCallEventData.getTrackingInfo()));
                System.out.println(approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), ACCEPTABLE_RT_DELTA));

        System.out.println(raBaseResourceCallEventData.getTrackingInfo() + "<<<<");

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
