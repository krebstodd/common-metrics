package com.blispay.common;

import com.blispay.common.metrics.BpCounter;
import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricService;
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

        final BpMetricService metricServiceInstance = BpMetricService.getInstance();
        final BpCounter counter2 = (BpCounter) metricServiceInstance.getMetric(BpMetricServiceTest.class, "defaultIncrementerTestCount");

        counter2.increment();

        assertEquals(2L, counter2.sample().getAttribute("count"));
        assertEquals(counter.sample().getAttribute("count"), counter2.sample().getAttribute("count"));
    }

    @Test
    public void testRemoveMetricByName() {
        metricService.createCounter(BpMetricServiceTest.class, "testRemoveMetricByName", "Test to ensure that we can remove a metric");
        assertTrue(metricService.getMetric(BpMetricServiceTest.class, "testRemoveMetricByName") instanceof BpCounter);
        metricService.removeMetric(BpMetricServiceTest.class, "testRemoveMetricByName");
        assertNull(metricService.getMetric(BpMetricServiceTest.class, "testRemoveMetricByName"));
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
