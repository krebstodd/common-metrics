package com.blispay.common;

import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricService;
import com.codahale.metrics.Metric;
import org.junit.Assert;

import java.lang.reflect.Method;

public abstract class AbstractMetricsTest {

    protected static final BpMetricService metricService = BpMetricService.getInstance("bpMetricsTest");

    protected <M extends Metric> M getInternalMetric(final BpMetric metric) {
        try {

            final Method internalMetricGetter = metric.getClass().getDeclaredMethod("getInternalMetric");
            internalMetricGetter.setAccessible(true);
            final M internalMetric = (M) internalMetricGetter.invoke(metric);
            internalMetricGetter.setAccessible(false);
            return internalMetric;
        } catch (Exception ex) {
            Assert.assertNull("There should be a package level getter for the internal dropwizard metric.", ex);
            return null;
        }
    }
}
