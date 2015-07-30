package com.blispay.common;

import com.blispay.common.metrics.ApplicationMonitor;
import com.codahale.metrics.MetricRegistry;

import java.lang.reflect.Field;

public abstract class AbstractMetricsTest {

    protected MetricRegistry getRegistry() throws IllegalAccessException, NoSuchFieldException {
        Field privateRegistry = null;
        try {
            privateRegistry = ApplicationMonitor.class.getDeclaredField("registry");
            privateRegistry.setAccessible(true);
            return (MetricRegistry) privateRegistry.get(null);
        } finally {
            if (privateRegistry != null) {
                privateRegistry.setAccessible(false);
            }
        }
    }

    protected Boolean inRange(final Double value, final Double min, final Double max) {
        return Double.compare(min, value) < 0 && Double.compare(value, max) < 0;
    }

}
