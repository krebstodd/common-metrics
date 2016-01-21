package com.blispay.common.metrics.util;

import com.blispay.common.metrics.metric.BpMetric;

public class RecordableEvent {

    private static final Level DEFAULT_LEVEL = Level.INFO;

    private final Level level;

    private final String message;

    private final BpMetric.Sample sample;

    public RecordableEvent(final BpMetric.EventSample sample) {
        this(DEFAULT_LEVEL, sample);
    }

    /**
     * Create a new recordable event containing the events warning level and the sample at time of the event.
     * @param level Level for logging purposes.
     * @param sample The event sample.
     */
    public RecordableEvent(final Level level, final BpMetric.EventSample sample) {
        this.level = level;
        this.message = sample.toString();
        this.sample = sample;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public BpMetric.Sample getSample() {
        return sample;
    }

    /**
     * The level at which the event should be logged.
     */
    public static enum Level {
        // CHECK_OFF: JavadocVariable
        TRACE, DEBUG, INFO, WARN, ERROR
        // CHECK_ON: JavadocVariable
    }

}
