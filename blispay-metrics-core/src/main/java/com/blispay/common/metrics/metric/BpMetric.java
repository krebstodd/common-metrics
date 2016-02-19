package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.event.NoOpEventEmitter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class BpMetric {

    private static final MetricContext emptyContext = new EmptyContext();

    private EventEmitter eventEmitter = new NoOpEventEmitter();

    private final MetricName mName;
    private final MetricClass mClass;
    private final MetricType mType;
    private Boolean emitEvents = Boolean.TRUE;

    public BpMetric(final MetricName mName, final MetricClass mClass, final MetricType mType) {
        this.mName = mName;
        this.mType = mType;
        this.mClass = mClass;
    }

    public void setEmitEvents(final Boolean doEmit) {
        this.emitEvents = doEmit;
    }

    public void setEventEmitter(final EventEmitter eventEmitter) {
        if (eventEmitter == null) {
            throw new IllegalArgumentException("Event emitter must not be null, use NoOpEventEmitter.");
        }

        this.eventEmitter = eventEmitter;
    }

    public MetricName getName() {
        return mName;
    }

    public MetricType getMetricType() {
        return mType;
    }

    public MetricClass getMetricClass() {
        return mClass;
    }

    protected void emitEvent(final Optional<MetricContext> context,
                             final Measurement measurement,
                             final MetricEvent.Level level) {

        if (emitEvents) {
            this.eventEmitter.emit(buildEvent(context, measurement, level));
        }
    }

    protected MetricEvent buildEvent(final Optional<MetricContext> context, final Measurement measurement, final MetricEvent.Level level) {
        return new MetricEvent(
                mName,
                mType,
                mClass,
                measurement,
                context.orElse(emptyContext),
                ZonedDateTime.now(ZoneId.of("UTC")),
                level);
    }

    protected static class EmptyContext extends MetricContext {

        @Override
        public Map<String, String> getContextMap() {
            return new HashMap<>();
        }

    }

}
