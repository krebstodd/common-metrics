package com.blispay.common.metrics.metric;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MetricName {

    private final String ROOT = "metrics";

    public abstract List<String> nameComponents();

    public String getValue() {
        final List<String> components = new LinkedList<>();
        components.add(0, ROOT);
        components.addAll(nameComponents());
        return components.stream().collect(Collectors.joining("."));
    }

    @Override
    public String toString() {
        return getValue();
    }
}

