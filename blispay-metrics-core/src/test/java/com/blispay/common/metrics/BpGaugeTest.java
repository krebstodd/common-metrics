package com.blispay.common.metrics;

// CHECK_OFF: MultipleStringLiterals
// CHECK_OFF: MagicNumber
public class BpGaugeTest extends AbstractMetricsTest {

//    @Test
//    public void testGenericGauge() {
//        final AtomicLong currentValue = new AtomicLong(0);
//
//        final Supplier<Long> supplier = () -> currentValue.get();
//
//        final BpGauge<Long> gauge = metricService.createGauge(new BusinessMetricName("gauge", "isHealthy"), MetricClass.businessEvent(), Measurement.Units.BOOL, supplier);
//
//        assertEquals(supplier.get(), gauge.getValue());
//        currentValue.getAndIncrement();
//        assertEquals(Long.valueOf(1L), supplier.get());
//        assertEquals(supplier.get(),  gauge.getValue());
//    }

}
// CHECK_ON: MagicNumber
// CHECK_OFF: MultipleStringLiterals
