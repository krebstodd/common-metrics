package com.blispay.common.metrics.legacy;

import com.blispay.common.metrics.AbstractMetricsTest;

// CHECK_OFF: MagicNumber
public class BpTimerTest extends AbstractMetricsTest {

//    @Test
//    public void testBpTimer() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
//        final MetricName metricName = new BusinessMetricName("test", "timer");
//
//        final BpTimer timer = metricService.createTimer(metricName, MetricClass.businessEvent());
//
//        for (int i = 1; i < 4; i++) {
//            final StopWatch resolver = timer.time();
//            Thread.sleep(i * 500);
//            resolver.stop();
//        }
//
//        assertEquals(3L, timer.getCount());
//        final double medianMillis = timer.getMedian() / 1000000;
//        assertTrue(approximatelyEqual(1000D, medianMillis, 100D));
//        final double meanMillis = timer.getMedian() / 1000000;
//        assertTrue(approximatelyEqual(1000D, meanMillis, 100D));
//    }

}
// CHECK_ON: MagicNumber
