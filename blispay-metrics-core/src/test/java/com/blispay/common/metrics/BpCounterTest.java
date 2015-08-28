package com.blispay.common.metrics;

//CHECK_OFF: AvoidStarImport

import org.junit.Test;

import static org.junit.Assert.assertEquals;
//CHECK_ON: AvoidStarImport

// CHECK_OFF: MultipleStringLiterals
// CHECK_OFF: MagicNumber
public class BpCounterTest extends AbstractMetricsTest {

    @Test
    public void testIncrementAndDecrement() {
        final BpCounter counter = metricService.createCounter(BpCounterTest.class, "defaultIncrementerTestCount", "Test the default incrementer.");

        assertEquals(0L, counter.sample().getAttribute("count"));
        counter.increment();
        assertEquals(1L, counter.sample().getAttribute("count"));
        counter.increment(5L);
        assertEquals(6L, counter.sample().getAttribute("count"));
        counter.decrement();
        assertEquals(5L, counter.sample().getAttribute("count"));
        counter.decrement(5L);
        assertEquals(0L, counter.sample().getAttribute("count"));
    }

}
// CHECK_ON: MagicNumber
// CHECK_ON: MultipleStringLiterals