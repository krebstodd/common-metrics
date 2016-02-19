package com.blispay.common.metrics;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class AbstractMetricsTest {

    static {
        System.setProperty("metrics.jmx.enabled", "true");
    }

    protected static final BpMetricService metricService = BpMetricService.globalInstance();

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

    protected Boolean approximatelyEqual(final Double expected, final Double actual, final Double acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
