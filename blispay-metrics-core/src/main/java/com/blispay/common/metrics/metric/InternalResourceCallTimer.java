package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.TrackingInfo;
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

    public StopWatch start(final InternalResource resource, final InternalAction action) {
        return start(resource, action, null);
    }

    public StopWatch start(final InternalResource resource, final InternalAction action, final TrackingInfo trackingInfo) {
        return start(new Context(Direction.INTERNAL, action, resource, trackingInfo));
    }

    @Override
    protected InternalResourceCallMetric buildEvent(final Context context) {
        final InternalResourceCallEventData eventData = new InternalResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus(),
                context.getTrackingInfo());

        return factory.newMetric(eventData);
    }

    protected static final class Context extends ResourceCallTimer.Context {

        private final InternalAction action;
        private final InternalResource resource;

        private Context(final Direction direction, final InternalAction action,
                       final InternalResource resource, final TrackingInfo trackingInfo) {

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
