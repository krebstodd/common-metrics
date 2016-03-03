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

    private final Matcher<String> resourceMatcher;
    private final Matcher<String> actionMatcher;
    private final Matcher<Integer> statusMatcher;
    private final Matcher<Direction> directionMatcher;
    private final Matcher trackingInfoMatcher;
    private final Long approxRuntime;
    private final Long acceptableRtDelta;

    public ResourceCallDataMatcher(final R resource, final A action, final Direction direction,
                                   final Status status, final Long approxMillis) {
        this(resource, action, direction, status, approxMillis, approxMillis, Matchers.nullValue(TrackingInfo.class));
    }

    public ResourceCallDataMatcher(final R resource, final A action, final Direction direction,
                                   final Status status, final Long approxMillis, final Matcher<TrackingInfo> trackingInfoMatcher) {
        this(resource, action, direction, status, approxMillis, approxMillis, trackingInfoMatcher);
    }


    /**
     * Match a resource call metric.
     * @param resource resource
     * @param action action
     * @param direction direction
     * @param status status
     * @param approxMillis approxMillis
     * @param acceptableRtDelta acceptable delta time in response approximation
     * @param trackingInfo trackinginfo
     */
    public ResourceCallDataMatcher(final R resource, final A action, final Direction direction,
                                   final Status status, final Long approxMillis, final Long acceptableRtDelta,
                                   final Matcher<TrackingInfo> trackingInfo) {

        this.resourceMatcher = Matchers.equalTo(resource.getValue());
        this.actionMatcher = Matchers.equalTo(action.getValue());
        this.directionMatcher = Matchers.equalTo(direction);
        this.statusMatcher = Matchers.equalTo(status.getValue());
        this.trackingInfoMatcher = trackingInfo;
        this.approxRuntime = approxMillis;
        this.acceptableRtDelta = acceptableRtDelta;
    }

    @Override
    public boolean matchesSafely(final BaseResourceCallEventData<R, A> raBaseResourceCallEventData) {
        System.out.println("START DATA ==============");
        System.out.println(resourceMatcher + " : " + raBaseResourceCallEventData.getResource().getValue());
        System.out.println(actionMatcher+ " : " + raBaseResourceCallEventData.getAction().getValue());
        System.out.println(approxRuntime + " :: " + raBaseResourceCallEventData.getDurationMillis());
        System.out.println(statusMatcher + " :: " + raBaseResourceCallEventData.getStatus());
        System.out.println(trackingInfoMatcher + " :: " + raBaseResourceCallEventData.getTrackingInfo());
        System.out.println(directionMatcher + " :: " + raBaseResourceCallEventData.getDirection());
        System.out.println(resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue()));
                System.out.println(actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue()));
                System.out.println(statusMatcher.matches(raBaseResourceCallEventData.getStatus()));
                System.out.println(directionMatcher.matches(raBaseResourceCallEventData.getDirection()));
                System.out.println(trackingInfoMatcher.matches(raBaseResourceCallEventData.getTrackingInfo()));
                System.out.println(approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), acceptableRtDelta));
        System.out.println("END DATA ==============");

        return resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue())
                && actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue())
                && statusMatcher.matches(raBaseResourceCallEventData.getStatus())
                && directionMatcher.matches(raBaseResourceCallEventData.getDirection())
                && trackingInfoMatcher.matches(raBaseResourceCallEventData.getTrackingInfo())
                && approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), acceptableRtDelta);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("resource=[")
                .appendDescriptionOf(resourceMatcher)
                .appendText("], action=[")
                .appendDescriptionOf(actionMatcher)
                .appendText("], status=[")
                .appendDescriptionOf(statusMatcher)
                .appendText("], direction=[")
                .appendDescriptionOf(directionMatcher)
                .appendText("], trackingInfo=[")
                .appendDescriptionOf(trackingInfoMatcher)
                .appendText("], approxRuntime=[")
                .appendValue(approxRuntime)
                .appendText("]");
    }

    protected Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
