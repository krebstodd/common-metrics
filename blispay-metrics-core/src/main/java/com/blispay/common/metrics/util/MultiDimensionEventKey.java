package com.blispay.common.metrics.util;

import org.json.JSONObject;

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
        return new JSONObject(nameValMap).toString();
    }
}
