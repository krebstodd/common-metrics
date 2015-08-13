package com.blispay.common;

import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricService;
import com.blispay.common.metrics.ImmutablePair;
import com.codahale.metrics.Metric;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMetricsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected static final BpMetricService metricService = BpMetricService.getInstance();

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

    protected Map<String, Object> toMap(final ImmutablePair[] sample) {
        final HashMap<String, Object> sampleMap = new HashMap<>(sample.length);
        for (int i  = 0; i < sample.length; i++) {
            sampleMap.put((String) sample[i].getKey(), sample[i].getVal());
        }
        return sampleMap;
    }

    protected Boolean approximatelyEqual(final Double expected, final Double actual, final Double acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

    protected Boolean approximatelyEqual(final Integer expected, final Integer actual, final Integer acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }
}
