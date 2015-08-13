package com.blispay.common;

import com.blispay.common.metrics.BpMeter;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BpMeterTest extends AbstractMetricsTest {

    @Test
    public void testBpMeter() throws InterruptedException {
        final BpMeter meter = metricService.createMeter(getClass(), "testBpMeter", "Test a bp meter");

        final long startMillis = Instant.now().toEpochMilli();
        meter.mark();
        meter.mark(3);
        meter.mark(500l);
        Thread.sleep(2000);
        final long stopMillis = Instant.now().toEpochMilli();

        final Map<String, Object> stats = toMap(meter.sample());
        assertEquals(504L, stats.get("count"));

        final Double meanRateSeconds = (Double) stats.get("meanRate");
        assertTrue(approximatelyEqual(250D, meanRateSeconds, 10D));
    }
}
