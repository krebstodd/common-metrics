package com.blispay.common.metrics;

import com.blispay.common.metrics.matchers.JsonEventDataMatcher;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.ConcurrentCollectionStrategy;
import com.blispay.common.metrics.report.SingleThreadedCollectionStrategy;
import com.blispay.common.metrics.report.Slf4jSnapshotReporter;
import com.blispay.common.metrics.report.SnapshotScheduler;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Class Slf4jSnapshotReporterTest.
 */
public class Slf4jSnapshotReporterTest extends AbstractMetricsTest {

    /**
     * Method testSlf4jSingleThreadedSnapshotReporter.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testSlf4jSingleThreadedSnapshotReporter() throws InterruptedException {
        final Logger log = mock(Logger.class);

        final MetricService metricService = new MetricService("appId");
        metricService.start();

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);

        metricService.utilizationGauge()
                     .inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                     .withName("thread-pool")
                     .register(() -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedDelay(Duration.ofMillis(750));
        final SingleThreadedCollectionStrategy collectionStrategy = new SingleThreadedCollectionStrategy();
        final Slf4jSnapshotReporter reporter = new Slf4jSnapshotReporter(log, scheduler, collectionStrategy);

        metricService.addSnapshotReporter(reporter);

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 0);
        expectedData.put("max", 100);
        expectedData.put("curr", 50);
        expectedData.put("util", 0.50);

        Thread.sleep(1000);

        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(log).info(argument.capture());

        // Parse the log line into a json object and test that it's the expected format for the event we just created.
        final List<String> logLines = argument.getAllValues();
        assertThat(new JSONObject(logLines.get(0)),
                   new JsonMetricMatcher(EventGroup.RESOURCE_UTILIZATION_THREADS,
                                         "appId",
                                         "thread-pool",
                                         EventType.RESOURCE_UTILIZATION,
                                         new JsonEventDataMatcher(expectedData),
                                         Matchers.nullValue()));

    }

    /**
     * Method testSlf4jMultiThreadedSnapshotReporter.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testSlf4jMultiThreadedSnapshotReporter() throws InterruptedException {
        final Logger log = mock(Logger.class);

        final MetricService metricService = new MetricService("appId");
        metricService.start();

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);

        metricService.utilizationGauge()
                     .inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                     .withName("thread-pool")
                     .register(() -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedDelay(Duration.ofMillis(750));
        final ConcurrentCollectionStrategy collectionStrategy = new ConcurrentCollectionStrategy(5, Duration.ofSeconds(10));
        final Slf4jSnapshotReporter reporter = new Slf4jSnapshotReporter(log, scheduler, collectionStrategy);

        metricService.addSnapshotReporter(reporter);

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 0);
        expectedData.put("max", 100);
        expectedData.put("curr", 50);
        expectedData.put("util", 0.50);

        Thread.sleep(1000);

        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(log).info(argument.capture());

        // Parse the log line into a json object and test that it's the expected format for the event we just created.
        final List<String> logLines = argument.getAllValues();
        assertThat(new JSONObject(logLines.get(0)),
                   new JsonMetricMatcher(EventGroup.RESOURCE_UTILIZATION_THREADS,
                                         "appId",
                                         "thread-pool",
                                         EventType.RESOURCE_UTILIZATION,
                                         new JsonEventDataMatcher(expectedData),
                                         Matchers.nullValue()));

    }

}
