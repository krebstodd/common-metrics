package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallEventData;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallMetric;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallMetricFactory;

public class InternalResourceCallTimer extends ResourceCallTimer<InternalResourceCallTimer.Context> {

    private final InternalResourceCallMetricFactory factory;

    public InternalResourceCallTimer(final EventEmitter emitter, final InternalResourceCallMetricFactory metricFactory) {
        super(emitter);
        this.factory = metricFactory;
    }

    @Override
    protected InternalResourceCallMetric buildEvent(final Context context) {
        final InternalResourceCallEventData eventData = new InternalResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getAction(),
                context.getResource(),
                context.getStatus());

        return factory.newMetric(eventData);
    }

    public static final class Context extends ResourceCallTimer.Context {

        private final InternalAction action;
        private final InternalResource resource;

        public Context(final Direction direction, final InternalAction action,
                       final InternalResource resource, final UserTrackingInfo trackingInfo) {

            super(direction, trackingInfo);
            this.action = action;
            this.resource = resource;
        }

        public InternalResource getResource() {
            return resource;
        }

        public InternalAction getAction() {
            return action;
        }
    }

}
