package com.blispay.common.metrics.util;

public class NotYetStartedException extends IllegalStateException {

    public NotYetStartedException(final String msg) {
        super(msg);
    }

    public NotYetStartedException(final Exception cause) {
        super(cause);
    }

}
