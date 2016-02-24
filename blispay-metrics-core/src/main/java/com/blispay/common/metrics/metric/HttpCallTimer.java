package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.call.http.HttpResourceCallEventData;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetric;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetricFactory;

public class HttpCallTimer extends ResourceCallTimer<HttpCallTimer.Context> {

    private final HttpResourceCallMetricFactory factory;

    public HttpCallTimer(final EventEmitter emitter, final HttpResourceCallMetricFactory metricFactory) {
        super(emitter);
        this.factory = metricFactory;
    }

    /**
     * Start a new stopwatch.
     *
     * @param direction Direction of the call.
     * @param resource The resource being called.
     * @param action The action being performed.
     * @return A running stopwatch instance.
     */
    public StopWatch start(final Direction direction,
                           final HttpResource resource,
                           final HttpAction action) {

        return start(direction, resource, action, null);

    }

    /**
     * Start a new stopwatch.
     *
     * @param direction Direction of the call.
     * @param resource The resource being called.
     * @param action The action being performed.
     * @param trackingInfo Tracking info for context.
     * @return A running stopwatch instance.
     */
    public StopWatch start(final Direction direction,
                           final HttpResource resource,
                           final HttpAction action,
                           final TrackingInfo trackingInfo) {

        return start(new Context(direction, action, resource, trackingInfo));

    }

    @Override
    protected HttpResourceCallMetric buildEvent(final Context context) {
        final HttpResourceCallEventData eventData = new HttpResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus(),
                context.getTrackingInfo());

        return factory.newMetric(eventData);
    }

    protected static final class Context extends ResourceCallTimer.Context {

        private final HttpAction action;
        private final HttpResource resource;

        private Context(final Direction direction, final HttpAction action,
                       final HttpResource resource, final TrackingInfo trackingInfo) {

            super(direction, trackingInfo);
            this.action = action;
            this.resource = resource;
        }

        public HttpResource getResource() {
            return resource;
        }

        public HttpAction getAction() {
            return action;
        }
    }

}
