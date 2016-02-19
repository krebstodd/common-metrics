package com.blispay.common.metrics.model.call;

public class Status {

    private final Integer value;

    public Status(final Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static Status success() {
        return new Status(0);
    }

    public static Status error() {
        return new Status(1);
    }

    public static Status warning() {
        return new Status(2);
    }

    public static Status warning(final Integer level) {
        if (level < 2) {
            return warning();
        } else {
            return new Status(level);
        }
    }

}
