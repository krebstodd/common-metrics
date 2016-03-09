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
import com.blispay.common.metrics.report.Snapshot;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JvmThreadMetricsTest {

    @Test
    public void testJvmThreadMetrics() throws InterruptedException {

        final MetricService serv = new MetricService(MetricTestUtil.randomAppId());
        serv.start();

        final BasicSnapshotReporter threadReporter = new BasicSnapshotReporter();
        serv.addSnapshotReporter(threadReporter);

        JvmProbe.start(serv);

        final Snapshot sn = threadReporter.report();

        final Matcher<Long> nnLong = Matchers.notNullValue(Long.class);
        final Matcher<Double> nnDbl = Matchers.notNullValue(Double.class);

        final EventMatcher<ResourceUtilizationData, Void> m1 = EventMatcher.<ResourceUtilizationData, Void>builder()
                .setApplication(serv.getApplicationId())
                .setGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .setName("jvm-active")
                .setType(EventType.RESOURCE_UTILIZATION)
                .setDataMatcher(new ResourceUtilizationDataMatcher(nnLong, nnLong, nnLong, nnDbl))
                .build();

        final EventMatcher<ResourceUtilizationData, Void> m2 = EventMatcher.<ResourceUtilizationData, Void>builder()
                .setApplication(serv.getApplicationId())
                .setGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .setName("jvm-active")
                .setType(EventType.RESOURCE_UTILIZATION)
                .setDataMatcher(new ResourceUtilizationDataMatcher(nnLong, nnLong, nnLong, nnDbl))
                .build();

        assertThat(sn.getMetrics(), Matchers.hasItem(m1));
        assertThat(sn.getMetrics(), Matchers.hasItem(m2));

        final CountDownLatch latch = new CountDownLatch(1);

        final int currActive = getActiveThreadMetric(threadReporter.report().getMetrics()).getData().getCurrentValue().intValue();

        // Assert that creating a new thread bumps the number of active threads.
        new Thread(() -> {

                final EventModel<ResourceUtilizationData, Void> activeThreads = getActiveThreadMetric(threadReporter.report().getMetrics());
                assertTrue(currActive < activeThreads.getData().getCurrentValue().intValue());

                latch.countDown();

            }).start();

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());

    }

    private EventModel<ResourceUtilizationData, Void> getActiveThreadMetric(final Set<EventModel> snapshot) {
        return snapshot.stream()
                .filter(model -> "jvm-active".equals(model.getHeader().getName()))
                .findAny()
                .map(model -> (EventModel<ResourceUtilizationData, Void>) model)
                .get();
    }

}
