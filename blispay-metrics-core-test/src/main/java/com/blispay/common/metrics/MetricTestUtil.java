package com.blispay.common.metrics;

import java.math.BigInteger;
import java.util.Random;

public final class MetricTestUtil {

    private MetricTestUtil() {

    }

    public static String randomAppId() {
        return new BigInteger(130, new Random()).toString(32);
    }
}
