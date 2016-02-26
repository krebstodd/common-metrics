package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallEventData;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallMetric;
import com.blispay.common.metrics.model.call.ds.DataSourceResourceCallMetricFactory;
import com.blispay.common.metrics.model.call.ds.DsAction;
import com.blispay.common.metrics.model.call.ds.DsResource;
import com.blispay.common.metrics.util.LocalMetricContext;

public class DatasourceCallTimer extends ResourceCallTimer<DatasourceCallTimer.Context> {

    private final DataSourceResourceCallMetricFactory factory;

    public DatasourceCallTimer(final EventEmitter emitter, final DataSourceResourceCallMetricFactory metricFactory) {
        super(metricFactory.getGroup(), metricFactory.getName(), emitter);
        this.factory = metricFactory;
    }

    /**
     * Start a new stopwatch.
     *
     * @param resource The resource being called.
     * @param action The action being performed.
     * @return A running stopwatch instance.
     */
    public StopWatch start(final DsResource resource,
                           final DsAction action) {

        return start(new Context(Direction.OUTBOUND, action, resource));

    }

    @Override
    protected DataSourceResourceCallMetric buildEvent(final Context context) {
        final DataSourceResourceCallEventData eventData = new DataSourceResourceCallEventData(
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

        private final DsAction action;
        private final DsResource resource;

        private Context(final Direction direction, final DsAction action, final DsResource resource) {

            super(direction);
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
