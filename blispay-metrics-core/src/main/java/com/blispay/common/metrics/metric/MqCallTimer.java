package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.mq.MqAction;
import com.blispay.common.metrics.model.call.mq.MqResource;
import com.blispay.common.metrics.model.call.mq.MqResourceCallEventData;
import com.blispay.common.metrics.model.call.mq.MqResourceCallMetric;
import com.blispay.common.metrics.model.call.mq.MqResourceCallMetricFactory;
import com.blispay.common.metrics.util.LocalMetricContext;

public class MqCallTimer extends ResourceCallTimer<MqCallTimer.Context> {

    private final MqResourceCallMetricFactory factory;

    public MqCallTimer(final EventEmitter emitter, final MqResourceCallMetricFactory metricFactory) {
        super(metricFactory.getGroup(), metricFactory.getName(), emitter);
        this.factory = metricFactory;
    }

    public StopWatch start(final MqResource resource, final MqAction action,
                           final String requestQueue, final String responseQueue, final String host,
                           final String requestType) {

        return start(new Context(Direction.OUTBOUND, action, resource, requestQueue, responseQueue, host, requestType));
    }

    @Override
    protected MqResourceCallMetric buildEvent(final Context context) {
        final MqResourceCallEventData eventData = new MqResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus(),
                LocalMetricContext.getTrackingInfo(),
                context.getRequestQueue(),
                context.getResponseQueue(),
                context.getHost(),
                context.getRequestType());

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

        private final MqAction action;
        private final MqResource resource;
        private final String requestQueue;
        private final String responseQueue;
        private final String host;
        private final String requestType;

        private Context(final Direction direction, final MqAction action,
                        final MqResource resource,
                        final String requestQueue, final String responseQueue,
                        final String host, final String requestType) {

            super(direction);
            this.action = action;
            this.resource = resource;
            this.requestQueue = requestQueue;
            this.responseQueue = responseQueue;
            this.host = host;
            this.requestType = requestType;
        }

        public MqResource getResource() {
            return resource;
        }

        public MqAction getAction() {
            return action;
        }

        public String getRequestQueue() {
            return requestQueue;
        }

        public String getResponseQueue() {
            return responseQueue;
        }

        public String getHost() {
            return host;
        }

        public String getRequestType() {
            return requestType;

        }
    }

}
