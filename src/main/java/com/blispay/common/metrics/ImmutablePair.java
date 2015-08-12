package com.blispay.common.metrics;

class ImmutablePair {

    private final Object key;

    private final Object val;

    public ImmutablePair(Object key, Object val) {
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
