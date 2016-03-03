package com.blispay.common.metrics.util;

import com.google.common.base.CaseFormat;

public final class NameFormatter {

    private NameFormatter() {

    }

    public static String toMetricName(final Class<?> clazz) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazz.getSimpleName());
    }

}
