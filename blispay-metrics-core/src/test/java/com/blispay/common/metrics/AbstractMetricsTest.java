package com.blispay.common.metrics;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMetricsTest {

    static {
        System.setProperty("metrics.jmx.enabled", "true");
    }

    protected static final BpMetricService metricService = BpMetricService.getInstance();

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

//    protected <M extends Metric> M getInternalMetric(final BpMetric metric) {
//        try {
//            final Method internalMetricGetter = metric.getClass().getDeclaredMethod("getInternalMetric");
//            internalMetricGetter.setAccessible(true);
//            final M internalMetric = (M) internalMetricGetter.invoke(metric);
//            internalMetricGetter.setAccessible(false);
//            return internalMetric;
//        // CHECK_OFF: IllegalCatch
//        } catch (Exception ex) {
//            Assert.assertNull("There should be a package level getter for the internal dropwizard metric.", ex);
//            return null;
//        }
//        // CHECK_ON: IllegalCatch
//    }

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
