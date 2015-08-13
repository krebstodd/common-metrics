package com.blispay.common;

//CHECK_OFF: AvoidStarImport
import com.blispay.common.metrics.BpCounter;
import com.codahale.metrics.Counter;
import org.junit.Test;

import static org.junit.Assert.*;
//CHECK_ON: AvoidStarImport

public class BpCounterTest extends AbstractMetricsTest {

    @Test
    public void testIncrementAndDecrement() {
        final BpCounter counter = metricService.createCounter(BpCounterTest.class, "defaultIncrementerTestCount", "Test the default incrementer.");
        final Counter internalCounter = getInternalMetric(counter);

        assertEquals(0, internalCounter.getCount());
        counter.increment();
        assertEquals(1, internalCounter.getCount());
        counter.increment(5l);
        assertEquals(6, internalCounter.getCount());
        counter.decrement();
        assertEquals(5, internalCounter.getCount());
        counter.decrement(5l);
        assertEquals(0, internalCounter.getCount());
    }

}
