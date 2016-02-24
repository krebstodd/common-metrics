package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.matchers.ResourceUtilizationMetricMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetric;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.SnapshotReporter;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class MemoryPoolMetricsTest {

    private static SnapshotReporter REPORTER;

    private final String metricName;

    public MemoryPoolMetricsTest(final String metricName) {
        this.metricName = metricName;
    }

    @Test
    public void testJvmProbe() {

        final ResourceUtilizationMetric metric = parseUtilizationMetrics(this.metricName);

        // non-heap utilization always has a max value of -1.
        if (metric.eventData().getMaxValue() != -1) {
            assertTrue(metric.eventData().getMinValue() <= metric.eventData().getMaxValue());
            assertTrue(metric.eventData().getCurrentValue() < metric.eventData().getMaxValue());
        }

        assertEquals(String.valueOf(metric.eventData().getCurrentPercentage()),
                String.valueOf((double) metric.eventData().getCurrentValue() / metric.eventData().getMaxValue()));

        assertThat(metric, new ResourceUtilizationMetricMatcher(
                MetricGroup.RESOURCE_UTILIZATION_MEM,
                metricName,
                MetricType.RESOURCE_UTILIZATION,
                Matchers.notNullValue(Long.class),
                Matchers.notNullValue(Long.class),
                Matchers.notNullValue(Long.class),
                Matchers.notNullValue(Double.class)));

    }

    private ResourceUtilizationMetric parseUtilizationMetrics(final String metricName) {

        return REPORTER.report().getMetrics()
                .stream()
                .filter(metric -> metric.getName().equals(metricName))
                .findFirst()
                .map(metric -> (ResourceUtilizationMetric) metric)
                .get();

    }

    /**
     * Set up test data.
     * @return test data.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{"metaspace-pool-utilization"},
            {"total-utilization"},
            {"compressed-class-space-pool-utilization"},
            {"heap-utilization"},
            {"ps-eden-space-pool-utilization"},
            {"non-heap-utilization"},
            {"ps-old-gen-pool-utilization"},
            {"code-cache-pool-utilization"},
            {"ps-survivor-space-pool-utilization"}
        });
    }

    /**
     * Setup the metric service.
     */
    @BeforeClass
    public static void setup() {

        final MetricService serv = new MetricService(MetricTestUtil.randomAppId());
        serv.start();

        REPORTER = new BasicSnapshotReporter();
        serv.addSnapshotReporter(REPORTER);

        final JvmProbe probe = new JvmProbe(serv);
        serv.addProbe(probe);

    }

}
