package com.blispay.common;

import com.blispay.common.metrics.BpHistogram;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BpHistogramTest extends AbstractMetricsTest {

    @Test
    public void testBpHistogram() throws InterruptedException {
        final BpHistogram meter = metricService.createHistogram(getClass(), "testBpHistogram", "Test a bp histogram");

        meter.update(1);
        meter.update(2l);

        final Map<String, Object> stats = toMap(meter.sample());
        assertEquals(2l, stats.get("count"));
        assertEquals(2l, stats.get("max"));
        assertEquals(1l, stats.get("min"));
        assertEquals(1.5d, stats.get("mean"));
    }
}
