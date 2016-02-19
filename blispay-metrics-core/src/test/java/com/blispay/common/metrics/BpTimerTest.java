package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.metric.BusinessMetricName;
import com.blispay.common.metrics.metric.MetricClass;
import com.blispay.common.metrics.metric.MetricName;
import com.blispay.common.metrics.util.StopWatch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// CHECK_OFF: MagicNumber
public class BpTimerTest extends AbstractMetricsTest {

    @Test
    public void testBpTimer() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        final MetricName metricName = new BusinessMetricName("test", "timer");

        final BpTimer timer = metricService.createTimer(metricName, MetricClass.businessEvent());

        for (int i = 1; i < 4; i++) {
            final StopWatch resolver = timer.time();
            Thread.sleep(i * 500);
            resolver.stop();
        }

        assertEquals(3L, timer.getCount());
        final double medianMillis = timer.getMedian() / 1000000;
        assertTrue(approximatelyEqual(1000D, medianMillis, 100D));
        final double meanMillis = timer.getMedian() / 1000000;
        assertTrue(approximatelyEqual(1000D, meanMillis, 100D));
    }

}
// CHECK_ON: MagicNumber
