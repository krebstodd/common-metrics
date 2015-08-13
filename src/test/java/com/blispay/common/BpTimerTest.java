package com.blispay.common;

import com.blispay.common.metrics.BpTimer;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BpTimerTest extends AbstractMetricsTest {

    @Test
    public void testBpTimer() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        final BpTimer timer = metricService.createTimer(getClass(), "testBpTimer", "Test that the bp timer wrapper works.");

        for (int i = 1; i < 4; i++) {
            final BpTimer.Resolver resolver = timer.time();
            Thread.sleep(i * 500);
            resolver.done();
        }

        final Map sample = toMap(timer.sample());
        assertEquals(3l, sample.get("count"));
        final double medianMillis = (Double) sample.get("median") / 1000000;
        assertTrue(approximatelyEqual(1000d, medianMillis, 100d));
        final double meanMillis = (Double) sample.get("mean") / 1000000;
        assertTrue(approximatelyEqual(1000d, meanMillis, 100d));
    }

}
