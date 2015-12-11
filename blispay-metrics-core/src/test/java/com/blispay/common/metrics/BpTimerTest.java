package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpTimer;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// CHECK_OFF: MagicNumber
public class BpTimerTest extends AbstractMetricsTest {

    @Test
    public void testBpTimer() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        final BpTimer timer = metricService.createTimer(getClass(), "testBpTimer", "Test that the bp timer wrapper works.");

        for (int i = 1; i < 4; i++) {
            final BpTimer.Resolver resolver = timer.time();
            Thread.sleep(i * 500);
            resolver.done();
        }

        final Map sample = toMap(timer.sample().getSampleData());
        assertEquals(3L, sample.get("count"));
        final double medianMillis = (Double) sample.get("median") / 1000000;
        assertTrue(approximatelyEqual(1000D, medianMillis, 100D));
        final double meanMillis = (Double) sample.get("mean") / 1000000;
        assertTrue(approximatelyEqual(1000D, meanMillis, 100D));
    }

}
// CHECK_ON: MagicNumber
