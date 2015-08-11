package com.blispay.common.metrics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BpScheduledReporter {

    private final ScheduledExecutorService executorService;

    private final Integer period;

    private final TimeUnit unit;

    public BpScheduledReporter(final Integer period, final TimeUnit unit) {
        this.period = period;
        this.unit = unit;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        this.executorService.scheduleAtFixedRate(this::report, period, period, unit);
    }

    public void stop() {
        this.executorService.shutdown();

        try {
            if (!this.executorService.awaitTermination(1L, TimeUnit.SECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
        }
    }

    public abstract void report();
}
