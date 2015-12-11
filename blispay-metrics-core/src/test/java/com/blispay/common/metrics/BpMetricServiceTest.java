package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpMetric;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// CHECK_OFF: MultipleStringLiterals
public class BpMetricServiceTest extends AbstractMetricsTest {

    @Test
    public void testIsSingleton() {
        final BpCounter counter = metricService.createCounter(BpMetricServiceTest.class, "defaultIncrementerTestCount", "Test the default incrementer.");

        assertEquals(0L, counter.sample().getAttribute("count"));
        counter.increment();

        final BpMetricService metricServiceInstance = BpMetricService.globalInstance();
        final BpCounter counter2 = (BpCounter) metricServiceInstance.getMetric(BpMetricServiceTest.class, "defaultIncrementerTestCount");

        counter2.increment();

        assertEquals(2L, counter2.sample().getAttribute("count"));
        assertEquals(counter.sample().getAttribute("count"), counter2.sample().getAttribute("count"));
    }

    @Test
    public void testRemoveMetricByObject() {
        final BpMetric metric = metricService.createCounter(BpMetricServiceTest.class, "testRemoveMetricByObject", "Test to ensure we can remove a metric given the obj.");
        assertTrue(metricService.getMetric(BpMetricServiceTest.class, "testRemoveMetricByObject") instanceof BpCounter);
        metricService.removeMetric(metric);
        assertNull(metricService.getMetric(BpMetricServiceTest.class, "testRemoveMetricByObject"));
    }

}
// CHECK_OFF: MultipleStringLiterals
