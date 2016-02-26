package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.counter.ResourceCounterMetric;
import com.blispay.common.metrics.model.counter.ResourceCounterMetricFactory;

public class ResourceCounter extends MetricRepository {

    private final ResourceCounterMetricFactory metricFactory;

    public ResourceCounter(final EventEmitter emitter, final ResourceCounterMetricFactory metricFactory) {
        super(metricFactory.getGroup(), metricFactory.getName(), emitter);
        this.metricFactory = metricFactory;
    }

    public void decrement(final Long decrementBy) {
        increment(negate(decrementBy));
    }

    public void increment(final Long incrementBy) {
        final ResourceCounterMetric metric = metricFactory.newMetric(incrementBy);
        save(metric);
    }

    private static Long negate(final Long absolute) {
        return absolute * -1L;
    }

    @Override
    public boolean equals(final Object other) {
        return computeEquals(this, other);
    }

    @Override
    public int hashCode() {
        return computeHashCode(this);
    }
}
