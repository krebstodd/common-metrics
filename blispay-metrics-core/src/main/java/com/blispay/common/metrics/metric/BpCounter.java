package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.MetricEvent;
import com.codahale.metrics.Counter;

import java.util.Optional;

public class BpCounter extends BpMetric {

    public static final MetricType mType = MetricType.COUNT;

    private final Counter counter;

    public BpCounter(final MetricName mName, final MetricClass mClass) {
        super(mName, mClass, mType);
        this.counter = new Counter();
    }

    public void increment(final MetricContext ec, final Long incrementBy) {
        updateCounter(Optional.of(ec), incrementBy);
    }

    public void increment(final Long incrementBy) {
        updateCounter(Optional.empty(), incrementBy);
    }

    public void decrement(final MetricContext ec, final Long decrementBy) {
        updateCounter(Optional.of(ec), negate(decrementBy));
    }

    public void decrement(final Long decrementBy) {
        updateCounter(Optional.empty(), negate(decrementBy));
    }

    public Long getCount() {
        return this.counter.getCount();
    }

    private void updateCounter(final Optional<MetricContext> ctx, final Long val) {
        counter.inc(val);

        emitEvent(ctx, new Measurement<>(val, Measurement.Units.TOTAL), determineLevel(val));
    }

    private MetricEvent.Level determineLevel(final Long val) {
        return MetricEvent.Level.INFO;
    }

    private static Long negate(final Long absolute) {
        return absolute * -1L;
    }

}
