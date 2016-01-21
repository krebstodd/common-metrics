package com.blispay.common.metrics.util;

public class RecordableEvent {

    private static final Level DEFAULT_LEVEL = Level.INFO;

    private final Level level;

    private final String message;

    public RecordableEvent(final String message) {
        this(DEFAULT_LEVEL, message);
    }

    public RecordableEvent(final Level level, final String message) {
        this.level = level;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
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
