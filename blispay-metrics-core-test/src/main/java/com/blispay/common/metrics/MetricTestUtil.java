package com.blispay.common.metrics;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Class MetricTestUtil.
 */
public final class MetricTestUtil {

    private static final Integer RANDOM_APPIDSIZE = 20;

    /**
     * Constructs MetricTestUtil.
     */
    private MetricTestUtil() {}

    /**
     * Method randomAppId.
     *
     * @return return value.
     */
    public static String randomAppId() {
        return RandomStringUtils.randomAlphabetic(RANDOM_APPIDSIZE);
    }

}
