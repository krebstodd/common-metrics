package com.blispay.common.metrics.util;

import com.google.common.base.CaseFormat;

/**
 * Class NameFormatter.
 */
public final class NameFormatter {

    /**
     * Constructs NameFormatter.
     */
    private NameFormatter() {}

    /**
     * Method toEventName.
     *
     * @param clazz clazz.
     * @return return value.
     */
    public static String toEventName(final Class<?> clazz) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazz.getSimpleName());
    }

}
