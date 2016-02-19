package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.codahale.metrics.Gauge;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BpGauge<T> extends BpMetric implements SnapshotProvider {

    private static final MetricType mType = MetricType.RESOURCE_UTILIZATION;

    private final Gauge<T> gauge;

    private Function<T, MetricEvent.Level> levelFn;

    private final Measurement.Units units;

    public BpGauge(final MetricName mName, final MetricClass mClass,
                   final Supplier<T> supplier, final Measurement.Units units) {
        super(mName, mClass, mType);

        this.gauge = supplier::get;
        this.units = units;
    }

    public T getValue() {
        return this.gauge.getValue();
    }

    @Override
    public MetricEvent snapshot() {
        final T currVal = getValue();
        return buildEvent(Optional.empty(), new Measurement<>(currVal, units), determineLevel(currVal));
    }

    public void setEventRecordLevelFn(final Function<T, MetricEvent.Level> fn) {
        this.levelFn = fn;
    }

    private MetricEvent.Level determineLevel(final T currValue) {
        if (levelFn != null) {
            return levelFn.apply(currValue);
        } else {
            return MetricEvent.Level.INFO;
        }
    }

}
