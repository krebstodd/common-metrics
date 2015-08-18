package com.blispay.common.metrics;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

// CHECK_OFF: MultipleStringLiterals
// CHECK_OFF: MagicNumber
public class BpGaugeTest extends AbstractMetricsTest {

    @Test
    public void testGenericGauge() {
        final AtomicLong currentValue = new AtomicLong(0);

        final Supplier<Long> supplier = () -> currentValue.get();

        final BpGauge<Long> gauge
                = metricService.createGauge(BpGaugeTest.class, "currentValue", "Basic test supplier", supplier);

        assertEquals(supplier.get(), gauge.sample().getAttribute("currentValue"));
        currentValue.getAndIncrement();
        assertEquals(Long.valueOf(1L), supplier.get());
        assertEquals(supplier.get(),  gauge.sample().getAttribute("currentValue"));
    }

}
// CHECK_ON: MagicNumber
// CHECK_OFF: MultipleStringLiterals
