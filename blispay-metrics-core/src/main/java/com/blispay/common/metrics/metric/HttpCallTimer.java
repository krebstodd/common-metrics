package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.call.http.HttpResourceCallEventData;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetric;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetricFactory;
import com.blispay.common.metrics.util.LocalMetricContext;

public class HttpCallTimer extends ResourceCallTimer<HttpCallTimer.Context> {

    private final HttpResourceCallMetricFactory factory;

    public HttpCallTimer(final EventEmitter emitter, final HttpResourceCallMetricFactory metricFactory) {

        super(metricFactory.getGroup(), metricFactory.getName(), emitter);
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

        return start(new Context(direction, action, resource));

    }

    @Override
    protected HttpResourceCallMetric buildEvent(final Context context) {
        final HttpResourceCallEventData eventData = new HttpResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus(),
                LocalMetricContext.getTrackingInfo());

        return factory.newMetric(eventData);
    }

    @Override
    public boolean equals(final Object other) {
        return computeEquals(this, other);
    }

    @Override
    public int hashCode() {
        return computeHashCode(this);
    }

    protected static final class Context extends ResourceCallTimer.Context {

        private final HttpAction action;
        private final HttpResource resource;

        protected Context(final Direction direction, final HttpAction action,
                       final HttpResource resource) {

            super(direction);
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
