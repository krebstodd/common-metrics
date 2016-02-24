package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.business.EventFactory;

public class EventRepository<T> extends MetricRepository {

    private final EventFactory<T> eventFactory;

    /**
     * Create a new repository for business events.
     *
     * @param eventEmitter Emitter for publishing business events.
     * @param eventFactory Factory for creating new business events.
     */
    public EventRepository(final EventEmitter eventEmitter,
                           final EventFactory<T> eventFactory) {
        super(eventEmitter);

        this.eventFactory = eventFactory;
    }

    public void save(final T eventData) {
        super.save(eventFactory.newMetric(eventData));
    }

}
