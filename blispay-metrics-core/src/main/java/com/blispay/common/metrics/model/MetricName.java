package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class MetricName {

    private static final String ROOT = "metrics";

    private final List<String> nameComponents;

    public MetricName() {
        this.nameComponents = new LinkedList<>();
        addComponent(ROOT);
    }

    protected void addComponent(final String nameComponent) {
        nameComponents.add(nameComponent.toLowerCase(Locale.ROOT));
    }

    @JsonValue
    public String getValue() {
        return toString();
    }

    @Override
    public String toString() {
        return String.join(".", nameComponents);
    }

}
