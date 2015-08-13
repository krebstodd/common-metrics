package com.blispay.common;

import com.blispay.common.metrics.BpCounter;
import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricService;
import com.codahale.metrics.Counter;
import org.junit.Test;

import static org.junit.Assert.*;

public class BpMetricServiceTest extends AbstractMetricsTest {

    @Test
    public void testOneServicePerProcess() {
        thrown.expect(IllegalArgumentException.class);
        BpMetricService.getInstance();
    }

    @Test
    public void testIsSingleton() {
        final BpCounter counter = metricService.createCounter(BpMetricServiceTest.class, "defaultIncrementerTestCount", "Test the default incrementer.");
        final Counter internalCounter = getInternalMetric(counter);

        assertEquals(0, internalCounter.getCount());
        counter.increment();

        final BpMetricService metricServiceInstance = BpMetricService.getInstance();
        final BpCounter counter2 = (BpCounter) metricService.getMetric(BpMetricServiceTest.class, "defaultIncrementerTestCount");
        final Counter internalCounter2 = getInternalMetric(counter2);

        counter2.increment();

        assertEquals(2, internalCounter.getCount());
        assertEquals(internalCounter.getCount(), internalCounter2.getCount());
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
