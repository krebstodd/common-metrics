package com.blispay.common.metrics.util;

/**
 * Enum StartupPhase.
 */
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

    /**
     * Constructs StartupPhase.
     *
     * @param phase phase.
     */
    StartupPhase(final Integer phase) {
        this.phase = phase;
    }

    /**
     * Method value.
     *
     * @return return value.
     */
    public int value() {
        return phase;
    }

}
