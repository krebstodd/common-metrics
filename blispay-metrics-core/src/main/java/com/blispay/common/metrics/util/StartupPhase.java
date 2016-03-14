package com.blispay.common.metrics.util;

public enum StartupPhase {

    /**
     * Dispatcher spring startup phase.
     */
    DISPATCHER(0),
    /**
     * Service spring startup phase. 
     */
    SERVICE(1),
    /**
     * Probe spring startup phase.
     */
    PROBE(2);

    private final Integer phase;

    private StartupPhase(final Integer phase) {
        this.phase = phase;
    }

    public int value() {
        return phase;
    }

}
