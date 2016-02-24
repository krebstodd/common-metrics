package com.blispay.common.metrics;

import org.apache.commons.lang3.RandomStringUtils;

public final class MetricTestUtil {

    private static final Integer randomAppIdSize = 20;

    private MetricTestUtil() {

    }

    public static String randomAppId() {
        return RandomStringUtils.randomAlphabetic(randomAppIdSize);
    }
}
