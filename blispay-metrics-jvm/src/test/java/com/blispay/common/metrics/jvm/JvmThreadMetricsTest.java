package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.matchers.ResourceUtilizationMetricMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.report.BasicSnapshotReporter;
import com.blispay.common.metrics.report.Snapshot;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class JvmThreadMetricsTest {

    @Test
    public void testJvmThreadMetrics() {

        final MetricService serv = new MetricService(MetricTestUtil.randomAppId());
        serv.start();

        final BasicSnapshotReporter threadReporter = new BasicSnapshotReporter();
        serv.addSnapshotReporter(threadReporter);

        serv.addProbe(new JvmProbe(serv));

        final Snapshot sn = threadReporter.report();

        final Matcher<Long> nnLong = Matchers.notNullValue();
        final Matcher<Double> nnDbl = Matchers.notNullValue();

        final Matcher activeThreadsGauge
                = new ResourceUtilizationMetricMatcher(MetricGroup.RESOURCE_UTILIZATION_THREADS, "jvm-active", MetricType.RESOURCE_UTILIZATION, nnLong, nnLong, nnLong, nnDbl);
        final Matcher blockedThreadsGauge
                = new ResourceUtilizationMetricMatcher(MetricGroup.RESOURCE_UTILIZATION_THREADS, "jvm-blocked", MetricType.RESOURCE_UTILIZATION, nnLong, nnLong, nnLong, nnDbl);

        assertThat(sn.getMetrics(), Matchers.hasItem(activeThreadsGauge));
        assertThat(sn.getMetrics(), Matchers.hasItem(blockedThreadsGauge));

        // Todo - create a new running thread and test that the number is bumped. 

    }

}
