package com.blispay.common.metrics.util;

public enum StartupPhase {

    DISPATCHER(0),
    SERVICE(1);

    private final Integer phase;

    private StartupPhase(final Integer phase) {
        this.phase = phase;
    }

    public int value() {
        return phase;
    }

}
