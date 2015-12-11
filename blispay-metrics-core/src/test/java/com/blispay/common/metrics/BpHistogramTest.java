package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpHistogram;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

// CHECK_OFF: MagicNumber
public class BpHistogramTest extends AbstractMetricsTest {

    @Test
    public void testBpHistogram() throws InterruptedException {
        final BpHistogram meter = metricService.createHistogram(getClass(), "testBpHistogram", "Test a bp histogram");

        meter.update(1);
        meter.update(2L);

        final Map<String, Object> stats = toMap(meter.sample().getSampleData());
        assertEquals(2L, stats.get("count"));
        assertEquals(2L, stats.get("max"));
        assertEquals(1L, stats.get("min"));
        assertEquals(1.5D, stats.get("mean"));
    }
}
// CHECK_ON: MagicNumber
