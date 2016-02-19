package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BusinessMetricName;
import com.blispay.common.metrics.metric.MetricName;
import com.blispay.common.metrics.metric.MetricClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

// CHECK_OFF: MultipleStringLiterals
public class BpMetricServiceTest extends AbstractMetricsTest {

    @Test
    public void testIsSingleton() {
        final MetricName counterName = new BusinessMetricName("testCounter", "count");
        final BpCounter counter = BpMetricService.globalInstance().createCounter(counterName, MetricClass.businessEvent());

        assertEquals(0L, counter.getCount().longValue());
        counter.increment(1L);

        final BpCounter counter2 = BpMetricService.globalInstance().createCounter(counterName, MetricClass.businessEvent());

        counter2.increment(1L);

        assertEquals(2L, counter2.getCount().longValue());
        assertEquals(counter.getCount().longValue(), counter2.getCount().longValue());
    }

    @Test
    public void testGetMetric() {
        final MetricName counterName = new BusinessMetricName("testCounter", "count");
        final BpCounter counter = BpMetricService.globalInstance().createCounter(counterName, MetricClass.businessEvent());

        assertNull(BpMetricService.globalInstance().getMetric("unknown"));
        assertEquals(counter, BpMetricService.globalInstance().getMetric(counterName.toString()));
    }

}
// CHECK_OFF: MultipleStringLiterals
