package com.blispay.common.metrics.probe;

import com.blispay.common.metrics.BpMetricService;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BpMetricProbe {

    protected static final BpMetricService metricService = BpMetricService.getInstance();

    private static final AtomicBoolean isRunning = new AtomicBoolean(false);

    private static Logger LOG;

    /**
     * Start the current metric probe running. Not all probe types will require logic in their start method, however most will.
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            startProbe();
        } else {
            LOG.warn("Probe already started, will not attempt a restart");
        }
    }

    protected abstract void startProbe();

    protected abstract Logger getLogger();
}
