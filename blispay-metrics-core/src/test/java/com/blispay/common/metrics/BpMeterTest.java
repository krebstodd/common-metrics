package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpMeter;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// CHECK_OFF: MagicNumber
public class BpMeterTest extends AbstractMetricsTest {

    @Test
    public void testBpMeter() throws InterruptedException {
        final BpMeter meter = metricService.createMeter(getClass(), "testBpMeter", "Test a bp meter");

        meter.mark();
        meter.mark(3);
        meter.mark(500L);

        Thread.sleep(2000);

        final Map<String, Object> stats = toMap(meter.aggregateSample().getSampleData());
        assertEquals(504L, stats.get("count"));

        final Double meanRateSeconds = (Double) stats.get("meanRate");
        assertTrue(approximatelyEqual(250D, meanRateSeconds, 10D));
    }
}
// CHECK_ON: MagicNumber
