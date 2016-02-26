package com.blispay.common.metrics;

import com.blispay.common.metrics.matchers.JsonEventDataMatcher;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.metric.EventRepository;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.report.Slf4jEventReporter;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class Slf4jEventReporterTest extends AbstractMetricsTest {

    @Test
    public void testSlf4jEventReporter() {
        final Logger log = mock(Logger.class);

        final MetricService metricService = new MetricService("appId");
        metricService.addEventSubscriber(new Slf4jEventReporter(log));
        metricService.start();

        final EventRepository<PiiBusinessEventData> repo
                = metricService.createEventRepository(MetricGroup.MERCHANT_DOMAIN, "create", PiiBusinessEventData.class);

        repo.save(defaultPiiBusinessEventData());

        // Note that userName should be filtered out by the pii jackson filter.
        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("notes", "Some notes");
        expectedData.put("count", 1);

        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(log).info(argument.capture());

        // Parse the log line into a json object and test that it's the expected format for the event we just created.
        final List<String> logLines = argument.getAllValues();
        assertThat(new JSONObject(logLines.get(0)), new JsonMetricMatcher(
                MetricGroup.MERCHANT_DOMAIN,
                "appId",
                "create",
                MetricType.EVENT,
                new JsonEventDataMatcher(expectedData)));

    }


}
