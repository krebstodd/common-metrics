package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpMetric;
import com.blispay.common.metrics.report.BpEventRecordingService;
import com.blispay.common.metrics.report.BpSlf4jEventReporter;
import com.blispay.common.metrics.report.DefaultBpEventRecordingService;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// CHECK_OFF: MultipleStringLiterals
public class BpMetricServiceTest extends AbstractMetricsTest {

    @Test
    public void testIsSingleton() {
        final BpCounter counter = metricService.createCounter(BpMetricServiceTest.class, "defaultIncrementerTestCount", "Test the default incrementer.");

        assertEquals(0L, counter.aggregateSample().getAttribute("count"));
        counter.increment();

        final BpMetricService metricServiceInstance = BpMetricService.globalInstance();
        final BpCounter counter2 = (BpCounter) metricServiceInstance.getMetric(BpMetricServiceTest.class, "defaultIncrementerTestCount");

        counter2.increment();

        assertEquals(2L, counter2.aggregateSample().getAttribute("count"));
        assertEquals(counter.aggregateSample().getAttribute("count"), counter2.aggregateSample().getAttribute("count"));
    }

    @Test
    public void testRemoveMetricByObject() {
        final BpMetric metric = metricService.createCounter(BpMetricServiceTest.class, "testRemoveMetricByObject", "Test to ensure we can remove a metric given the obj.");
        assertTrue(metricService.getMetric(BpMetricServiceTest.class, "testRemoveMetricByObject") instanceof BpCounter);
        metricService.removeMetric(metric);
        assertNull(metricService.getMetric(BpMetricServiceTest.class, "testRemoveMetricByObject"));
    }

    @Test
    public void testLocalService() {
        final BpEventRecordingService recordingService = new DefaultBpEventRecordingService();
        recordingService.addEventReporter(new BpSlf4jEventReporter(LoggerFactory.getLogger(getClass())));

        final BpMetricService localService = new BpMetricService(recordingService);

        final BpCounter counter = localService.createCounter(BpMetricServiceTest.class, "defaultIncrementerTestCount", "Test the default incrementer.");

        assertEquals(0L, counter.aggregateSample().getAttribute("count"));
        counter.increment();
    }

}
// CHECK_OFF: MultipleStringLiterals
