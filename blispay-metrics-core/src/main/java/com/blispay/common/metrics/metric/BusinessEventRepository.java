package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.UserTrackingInfo;
import com.blispay.common.metrics.model.business.EventFactory;

public class BusinessEventRepository<T> extends MetricRepository {

    private final EventFactory eventFactory;

    public BusinessEventRepository(final EventEmitter eventEmitter,
                                   final EventFactory eventFactory) {
        super(eventEmitter);

        this.eventFactory = eventFactory;
    }

    public void save(final UserTrackingInfo trackingInfo, final T eventData) {
        super.save(eventFactory.newMetric(trackingInfo, eventData));
    }

}
