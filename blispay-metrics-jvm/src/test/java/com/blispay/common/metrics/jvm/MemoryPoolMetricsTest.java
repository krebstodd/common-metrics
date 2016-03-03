package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.ResourceUtilizationDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
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

    private static final String application = MetricTestUtil.randomAppId();

    private static SnapshotReporter REPORTER;

    private final String metricName;

    public MemoryPoolMetricsTest(final String metricName) {
        this.metricName = metricName;
    }

    @Test
    public void testJvmProbe() {

        final EventModel<ResourceUtilizationData> event = parseUtilizationMetrics(this.metricName);

        // non-heap utilization always has a max value of -1.
        if (event.eventData().getMaxValue() != -1) {
            assertTrue(event.eventData().getMinValue() <= event.eventData().getMaxValue());
            assertTrue(event.eventData().getCurrentValue() < event.eventData().getMaxValue());
        }

        assertEquals(String.valueOf(event.eventData().getCurrentPercentage()),
                String.valueOf((double) event.eventData().getCurrentValue() / event.eventData().getMaxValue()));

        assertThat(event, new EventMatcher<>(
                application,
                EventGroup.RESOURCE_UTILIZATION_MEM,
                metricName,
                EventType.RESOURCE_UTILIZATION,
                new ResourceUtilizationDataMatcher(Matchers.notNullValue(Long.class), Matchers.notNullValue(Long.class), Matchers.notNullValue(Long.class), Matchers.notNullValue(Double.class))));

    }

    private EventModel<ResourceUtilizationData> parseUtilizationMetrics(final String metricName) {

        return REPORTER.report().getMetrics()
                .stream()
                .filter(metric -> metric.getName().equals(metricName))
                .findFirst()
                .map(metric -> (EventModel<ResourceUtilizationData>) metric)
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

        final MetricService serv = new MetricService(application);
        serv.start();

        REPORTER = new BasicSnapshotReporter();
        serv.addSnapshotReporter(REPORTER);

        JvmProbe.start(serv);

    }

}
