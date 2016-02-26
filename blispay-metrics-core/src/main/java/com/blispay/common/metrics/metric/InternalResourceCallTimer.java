package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallEventData;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallMetric;
import com.blispay.common.metrics.model.call.internal.InternalResourceCallMetricFactory;
import com.blispay.common.metrics.util.LocalMetricContext;

public class InternalResourceCallTimer extends ResourceCallTimer<InternalResourceCallTimer.Context> {

    private final InternalResourceCallMetricFactory factory;

    public InternalResourceCallTimer(final EventEmitter emitter,
                                     final InternalResourceCallMetricFactory metricFactory) {
        super(metricFactory.getGroup(), metricFactory.getName(), emitter);
        this.factory = metricFactory;
    }

    public StopWatch start(final InternalResource resource, final InternalAction action) {
        return start(new Context(Direction.INTERNAL, action, resource));
    }

    @Override
    protected InternalResourceCallMetric buildEvent(final Context context) {
        final InternalResourceCallEventData eventData = new InternalResourceCallEventData(
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

        private final InternalAction action;
        private final InternalResource resource;

        private Context(final Direction direction, final InternalAction action,
                       final InternalResource resource) {

            super(direction);
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
