package com.blispay.common.metrics.metric;

public class Measurement<T> {

    private static final String valueKey = "value";
    private static final String unitsKey = "units";

    private final T value;

    private final Units unit;

    public Measurement(final T value, final Units unit) {
        this.value = value;
        this.unit = unit;
    }

    public String getUnitsKey() {
        return unitsKey;
    }

    public String getValueKey() {
        return valueKey;
    }

    public T getValue() {
        return value;
    }

    public String getUnits() {
        return unit.getValue();
    }

    public static enum Units {

        MILLISECONDS("MS"),
        PERCENTAGE("PCT"),
        TOTAL("TOTAL"),
        PER_MILLISECOND("PER_MS"),
        BOOL("BOOL");

        private String val;

        private Units(final String val) {
            this.val = val;
        }

        public String getValue() {
            return val;
        }

    }
}
