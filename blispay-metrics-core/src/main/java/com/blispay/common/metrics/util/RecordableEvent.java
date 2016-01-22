package com.blispay.common.metrics.util;

public class RecordableEvent<T> extends MetricEvent<T> {

    private static final Level DEFAULT_LEVEL = Level.INFO;

    private final Level level;

    public RecordableEvent(final MetricEvent<T> baseEvent, final Level level) {
        this(level, baseEvent.getOwner(), baseEvent.getMetricName(), baseEvent.getEventKey(), baseEvent.getValue());
    }

    public RecordableEvent(final Class<?> owner, final String metricName, final String eventKey, final T eventValue) {
        this(DEFAULT_LEVEL, owner, metricName, eventKey, eventValue);
    }

    /**
     * Create a new recordable event containing the events warning level and the sample at time of the event.
     *
     * @param level Level for logging purposes.
     * @param owner The event owning class.
     * @param metricName Metric name.
     * @param eventKey Event description.
     * @param eventValue The value of the event.
     */
    public RecordableEvent(final Level level, final Class<?> owner, final String metricName, final String eventKey, final T eventValue) {
        super(owner, metricName, eventKey, eventValue);
        this.level = level;
    }

    public Level getLevel() {
        return level;
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
