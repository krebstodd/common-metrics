package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.BaseMetricModel;
import com.blispay.common.metrics.model.MetricGroup;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class MetricRepository {

    private final String name;
    private final MetricGroup group;
    private final EventEmitter eventEmitter;
    private Boolean eventsEnabled = Boolean.TRUE;

    public MetricRepository(final MetricGroup group,
                            final String name,
                            final EventEmitter eventEmitter) {

        this.name = name;
        this.group = group;
        this.eventEmitter = eventEmitter;

    }

    protected void save(final BaseMetricModel event) {
        if (eventsEnabled) {
            this.eventEmitter.emit(event);
        }
    }

    public void disableEvents() {
        this.eventsEnabled = Boolean.FALSE;
    }

    public void enableEvents() {
        this.eventsEnabled = Boolean.TRUE;
    }

    public MetricGroup getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public abstract boolean equals(Object other);

    public abstract int hashCode();

    protected static boolean computeEquals(final MetricRepository me, final Object other) {

        if (other == null || !(other instanceof MqCallTimer)) {
            return false;
        }

        if (other == me) {
            return true;
        }

        final MqCallTimer otherTimer = (MqCallTimer) other;

        return new EqualsBuilder()
                .append(me.getGroup(), otherTimer.getGroup())
                .append(me.getName(), ((MqCallTimer) other).getName())
                .isEquals();

    }

    protected static int computeHashCode(final MetricRepository repository) {

        return new HashCodeBuilder(17, 31)
                .append(repository.getClass())
                .append(repository.getGroup())
                .append(repository.getName())
                .toHashCode();

    }

}
