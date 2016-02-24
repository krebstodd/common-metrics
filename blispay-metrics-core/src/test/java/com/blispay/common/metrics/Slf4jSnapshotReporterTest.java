package com.blispay.common.metrics;

import com.blispay.common.metrics.matchers.JsonEventDataMatcher;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.Slf4jSnapshotReporter;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class Slf4jSnapshotReporterTest extends AbstractMetricsTest {

    @Test
    public void testSlf4jSnapshotReporter() throws InterruptedException {
        final Logger log = mock(Logger.class);

        final MetricService metricService = new MetricService("appId");
        metricService.start();

        final Long min = 0L;
        final Long max = 100L;
        final AtomicLong curVal = new AtomicLong(50L);
        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "thread-pool",
                () -> new ResourceUtilizationData(min, max, curVal.get(), (double) curVal.get() / max));

        metricService.addSnapshotReporter(new Slf4jSnapshotReporter(log, 750, TimeUnit.MILLISECONDS));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 0);
        expectedData.put("max", 100);
        expectedData.put("curr", 50);
        expectedData.put("util", 0.50);

        Thread.sleep(1000);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(log).info(argument.capture());

        // Parse the log line into a json object and test that it's the expected format for the event we just created.
        final List<String> logLines = argument.getAllValues();
        assertThat(new JSONObject(logLines.get(0)), new JsonMetricMatcher(
                MetricGroup.RESOURCE_UTILIZATION_THREADS,
                "appId",
                "thread-pool",
                MetricType.RESOURCE_UTILIZATION,
                new JsonEventDataMatcher(expectedData)));

    }


}
