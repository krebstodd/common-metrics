package com.blispay.common.metrics.util;

import java.util.Map;
import java.util.TreeMap;

public class MultiDimensionEventKey implements MetricEventKey {

    /**
     * Use tree map to ensure deterministic key ordering.
     */
    private final Map<String, String> nameValMap = new TreeMap<>();

    public MultiDimensionEventKey() { }

    public MultiDimensionEventKey dimension(final String name, final String value) {
        nameValMap.put(name, value);
        return this;
    }

    @Override
    public String buildKey() {
        return nameValMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=[" + entry.getValue() + "]")
                .reduce((accum, keyVal) -> accum + "," + keyVal)
                .orElse("");
    }
}
