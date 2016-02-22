package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallEventData;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallMetric;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallMetricFactory;
import com.blispay.common.metrics.model.call.ds.DsAction;
import com.blispay.common.metrics.model.call.ds.DsResource;

public class DatasourceCallTimer extends ResourceCallTimer<DatasourceCallTimer.Context> {

    private final DataSourceResourceCallMetricFactory factory;

    public DatasourceCallTimer(final EventEmitter emitter, final DataSourceResourceCallMetricFactory metricFactory) {
        super(emitter);
        this.factory = metricFactory;
    }

    public StopWatch start(final DsResource resource,
                           final DsAction action,
                           final UserTrackingInfo trackingInfo) {

        return start(new Context(Direction.OUTBOUND, action, resource, trackingInfo));

    }

    @Override
    protected DataSourceResourceCallMetric buildEvent(final Context context) {
        final DataSourceResourceCallEventData eventData = new DataSourceResourceCallEventData(
                context.getDirection(),
                context.getDuration().toMillis(),
                context.getResource(),
                context.getAction(),
                context.getStatus());

        return factory.newMetric(eventData);
    }

    public static final class Context extends ResourceCallTimer.Context {

        private final DsAction action;
        private final DsResource resource;

        public Context(final Direction direction, final DsAction action,
                       final DsResource resource, final UserTrackingInfo trackingInfo) {

            super(direction, trackingInfo);
            this.action = action;
            this.resource = resource;
        }

        public DsResource getResource() {
            return resource;
        }

        public DsAction getAction() {
            return action;
        }
    }

}
