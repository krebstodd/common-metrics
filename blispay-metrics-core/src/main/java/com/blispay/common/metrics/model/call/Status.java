package com.blispay.common.metrics.model.call;

/**
 * Class Status.
 */
public class Status {

    private final Integer value;

    /**
     * Constructs Status.
     *
     * @param value value.
     */
    public Status(final Integer value) {
        this.value = value;
    }

    /**
     * Method getValue.
     *
     * @return return value.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Method success.
     *
     * @return return value.
     */
    public static Status success() {
        return new Status(0);
    }

    /**
     * Method error.
     *
     * @return return value.
     */
    public static Status error() {
        return new Status(1);
    }

    /**
     * Method warning.
     *
     * @return return value.
     */
    public static Status warning() {
        return new Status(2);
    }

    /**
     * Warning status with specific level.
     * @param level level
     * @return status instance
     */
    public static Status warning(final Integer level) {
        if (level < 2) {
            return warning();
        } else {
            return new Status(level);
        }
    }

    /**
     * Method fromValue.
     *
     * @param val val.
     * @return return value.
     */
    public static Status fromValue(final Integer val) {
        return new Status(val);
    }

}
