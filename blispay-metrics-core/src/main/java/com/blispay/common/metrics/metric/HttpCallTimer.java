package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.UserTrackingInfo;
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

    public StopWatch start(final Direction direction,
                           final HttpResource resource,
                           final HttpAction action,
                           final UserTrackingInfo trackingInfo) {

        return start(new Context(direction, action, resource, trackingInfo));

    }

    @Override
    protected HttpResourceCallMetric buildEvent(final Context context) {
        final HttpResourceCallEventData eventData = new HttpResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus());

        return factory.newMetric(eventData);
    }

    protected static final class Context extends ResourceCallTimer.Context {

        private final HttpAction action;
        private final HttpResource resource;

        public Context(final Direction direction, final HttpAction action,
                       final HttpResource resource, final UserTrackingInfo trackingInfo) {

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
