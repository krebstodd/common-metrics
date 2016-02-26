package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.business.EventFactory;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventRepository<T> extends MetricRepository {

    private final EventFactory<T> eventFactory;
    private final Class<T> eventDataType;

    /**
     * Create a new repository for business events.
     *
     * @param eventEmitter Emitter for publishing business events.
     * @param eventFactory Factory for creating new business events.
     */
    public EventRepository(final EventEmitter eventEmitter,
                           final EventFactory<T> eventFactory,
                           final Class<T> eventDataType) {
        super(eventFactory.getGroup(), eventFactory.getName(), eventEmitter);

        this.eventFactory = eventFactory;
        this.eventDataType = eventDataType;
    }

    @Override
    public boolean equals(final Object other) {
        final Boolean baseEquals = computeEquals(this, other);

        if (baseEquals) {
            return ((EventRepository) other).getEventDataType().equals(this.getEventDataType());
        } else {
            return baseEquals;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(computeHashCode(this))
                .append(eventDataType)
                .toHashCode();
    }

    public void save(final T eventData) {
        super.save(eventFactory.newMetric(eventData));
    }

    public Class<T> getEventDataType() {
        return eventDataType;
    }
}
