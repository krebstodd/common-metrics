package com.blispay.common.metrics;

import com.codahale.metrics.Metric;

import java.util.HashMap;

public abstract class BpMetric implements Metric {

    private final String name;

    private final String description;

    public BpMetric(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    abstract <M extends Metric> M getInternalMetric();

    abstract ImmutablePair[] sample();

}
