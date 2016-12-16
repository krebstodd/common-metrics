package com.blispay.common.metrics.util;

/**
 * Class NotYetStartedException.
 */
public class NotYetStartedException extends IllegalStateException {

    /**
     * Constructs NotYetStartedException.
     *
     * @param msg msg.
     */
    public NotYetStartedException(final String msg) {
        super(msg);
    }

    /**
     * Constructs NotYetStartedException.
     *
     * @param cause cause.
     */
    public NotYetStartedException(final Exception cause) {
        super(cause);
    }

}
