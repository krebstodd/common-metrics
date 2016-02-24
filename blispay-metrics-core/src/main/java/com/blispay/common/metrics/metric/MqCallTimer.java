package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.mq.MqAction;
import com.blispay.common.metrics.model.call.mq.MqResource;
import com.blispay.common.metrics.model.call.mq.MqResourceCallEventData;
import com.blispay.common.metrics.model.call.mq.MqResourceCallMetric;
import com.blispay.common.metrics.model.call.mq.MqResourceCallMetricFactory;

public class MqCallTimer extends ResourceCallTimer<MqCallTimer.Context> {

    private final MqResourceCallMetricFactory factory;

    public MqCallTimer(final EventEmitter emitter, final MqResourceCallMetricFactory metricFactory) {
        super(emitter);
        this.factory = metricFactory;
    }

    public StopWatch start(final MqResource resource, final MqAction action, final UserTrackingInfo trackingInfo) {
        return start(new Context(Direction.OUTBOUND, action, resource, trackingInfo));
    }

    @Override
    protected MqResourceCallMetric buildEvent(final Context context) {
        final MqResourceCallEventData eventData = new MqResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus(),
                context.getTrackingInfo());

        return factory.newMetric(eventData);
    }

    public static final class Context extends ResourceCallTimer.Context {

        private final MqAction action;
        private final MqResource resource;

        public Context(final Direction direction, final MqAction action,
                       final MqResource resource, final UserTrackingInfo trackingInfo) {

            super(direction, trackingInfo);
            this.action = action;
            this.resource = resource;
        }

        public MqResource getResource() {
            return resource;
        }

        public MqAction getAction() {
            return action;
        }
    }

}
