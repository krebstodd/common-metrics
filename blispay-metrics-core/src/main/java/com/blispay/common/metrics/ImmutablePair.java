package com.blispay.common.metrics;

public class ImmutablePair {

    private final Object key;

    private final Object val;

    public ImmutablePair(final Object key, final Object val) {
        this.key = key;
        this.val = val;
    }

    public Object getKey() {
        return key;
    }

    public Object getVal() {
        return val;
    }

}
