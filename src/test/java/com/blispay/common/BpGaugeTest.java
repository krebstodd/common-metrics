package com.blispay.common;

import com.blispay.common.metrics.BpGauge;
import com.codahale.metrics.Gauge;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class BpGaugeTest extends AbstractMetricsTest {

    @Test
    public void testGenericGauge() {
        final AtomicLong currentValue = new AtomicLong(0);

        final Supplier<Long> supplier = () -> currentValue.get();

        final BpGauge<Long> gauge
                = metricService.createGauge(BpGaugeTest.class, "currentValue", "Basic test supplier", supplier);
        final Gauge internal = getInternalMetric(gauge);

        assertEquals(supplier.get(), internal.getValue());
        currentValue.getAndIncrement();
        assertEquals(Long.valueOf(1l), supplier.get());
        assertEquals(supplier.get(), internal.getValue());
    }

}
