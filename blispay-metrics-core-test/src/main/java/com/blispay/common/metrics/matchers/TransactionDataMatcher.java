package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.TrackingInfo;
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
    private final Matcher<TrackingInfo> trackingInfoMatcher;
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
     * @param acceptableDelta acceptableDelta
     * @param approxMillis approxMillis
     */
    public TransactionDataMatcher(final Resource resource, final Action action, final Direction direction,
                                  final Status status, final Long approxMillis, final Long acceptableDelta) {

        this.resourceMatcher = Matchers.equalTo(resource.getValue());
        this.actionMatcher = Matchers.equalTo(action.getValue());
        this.directionMatcher = Matchers.equalTo(direction);
        this.statusMatcher = Matchers.equalTo(status.getValue());
        this.trackingInfoMatcher = Matchers.nullValue(TrackingInfo.class);
        this.approxRuntime = approxMillis;
        this.acceptableDelta = acceptableDelta;
    }

    /**
     * Match a resource call metric.
     * @param resource resource
     * @param action action
     * @param direction direction
     * @param status status
     * @param approxMillis approxMillis
     * @param trackingInfo trackinginfo
     */
    public TransactionDataMatcher(final Resource resource, final Action action, final Direction direction,
                                  final Status status, final Long approxMillis, final Matcher<TrackingInfo> trackingInfo) {

        this(resource, action, direction, status, approxMillis, approxMillis, trackingInfo);
    }

    /**
     * Match a resource call metric.
     * @param resource resource
     * @param action action
     * @param direction direction
     * @param status status
     * @param approxMillis approxMillis
     * @param acceptableDelta acceptableDelta
     * @param trackingInfo trackinginfo
     */
    public TransactionDataMatcher(final Resource resource, final Action action, final Direction direction,
                                  final Status status, final Long approxMillis, final Long acceptableDelta, final Matcher<TrackingInfo> trackingInfo) {

        this.resourceMatcher = Matchers.equalTo(resource.getValue());
        this.actionMatcher = Matchers.equalTo(action.getValue());
        this.directionMatcher = Matchers.equalTo(direction);
        this.statusMatcher = Matchers.equalTo(status.getValue());
        this.trackingInfoMatcher = trackingInfo;
        this.approxRuntime = approxMillis;
        this.acceptableDelta = acceptableDelta;
    }

    @Override
    public boolean matchesSafely(final TransactionData raBaseResourceCallEventData) {

//        System.out.println("START TRANSACTION DATA ______");
//        System.out.println(resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue()));
//                System.out.println(actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue()));
//                System.out.println(statusMatcher.matches(raBaseResourceCallEventData.getStatus()));
//                System.out.println(directionMatcher.matches(raBaseResourceCallEventData.getDirection()));
//                System.out.println(trackingInfoMatcher.matches(raBaseResourceCallEventData.getTrackingInfo()));
//                System.out.println(approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), acceptableDelta));
//        System.out.println("END TRANSACTION DATA ______");

        return resourceMatcher.matches(raBaseResourceCallEventData.getResource().getValue())
                && actionMatcher.matches(raBaseResourceCallEventData.getAction().getValue())
                && statusMatcher.matches(raBaseResourceCallEventData.getStatus())
                && directionMatcher.matches(raBaseResourceCallEventData.getDirection())
                && trackingInfoMatcher.matches(raBaseResourceCallEventData.getTrackingInfo())
                && approximatelyEqual(approxRuntime, raBaseResourceCallEventData.getDurationMillis(), acceptableDelta);
    }

    @Override
    public void describeTo(final Description description) {

    }

    protected Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}