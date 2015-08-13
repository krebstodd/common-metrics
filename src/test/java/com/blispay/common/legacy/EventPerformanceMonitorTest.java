package com.blispay.common.legacy;

import com.blispay.common.metrics.legacy.ApplicationMonitor;
import com.blispay.common.metrics.legacy.EventPerformanceMonitor;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Assert;
import org.junit.Test;

public class EventPerformanceMonitorTest extends AbstractMetricsTest {

    //CHECK_OFF: MagicNumber
    //CHECK_OFF: MultipleStringLiterals
    @Test
    public void testNewEventMetric() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        final String metricNamespace = "namespace";
        final String metricA = "metricA";
        final String metricB = "metricB";
        final String metricBCategory = "metricBCategory";
        final EventPerformanceMonitor monitor = EventPerformanceMonitor.getMonitor(metricNamespace);

        final ApplicationMonitor.MetricResolver aResolver = monitor.start(metricA);
        Thread.sleep(500);
        aResolver.done();

        final ApplicationMonitor.MetricResolver bResolver1 = monitor.start(metricB, metricBCategory);
        Thread.sleep(500);
        bResolver1.done();

        final ApplicationMonitor.MetricResolver bResolver2 = monitor.start(metricB, metricBCategory);
        Thread.sleep(1000);
        bResolver2.done();

        // These should be close approximations, runtime overhead might push them a milli in either direction.
        final MetricRegistry registry = getRegistry();
        final Timer timerA = registry.getTimers().get(metricNamespace + "." + metricA + ".response-time");
        final Timer timerB = registry.getTimers().get(metricNamespace + "." + metricB + "." + metricBCategory + ".response-time");

        // Mean responses come back in nanoseconds.
        final Double meanResponseTimeA = timerA.getSnapshot().getMean() / 100000000;
        final Double meanResponseTimeB = timerB.getSnapshot().getMean() / 100000000;

        Assert.assertTrue("metric a is around 500 milliseconds.", inRange(meanResponseTimeA, new Double(5.0), new Double(6.0)));
        Assert.assertEquals("Count is correct", 1, timerA.getCount());
        Assert.assertTrue("Metric b is around 750 milliseconds", inRange(meanResponseTimeB, new Double(7.5), new Double(8.5)));
        Assert.assertEquals("Count is correct", 2, timerB.getCount());
    }
    //CHECK_ON: MagicNumber
    //CHECK_ON: MultipleStringLiterals
}
