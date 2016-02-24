package com.blispay.common.metrics;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.SecureObjectMapper;
import com.blispay.common.metrics.matchers.JsonEventDataMatcher;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.business.EventMetric;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.call.http.HttpResourceCallEventData;
import com.blispay.common.metrics.model.call.http.HttpResourceCallMetric;
import com.blispay.common.metrics.model.counter.ResourceCounterEventData;
import com.blispay.common.metrics.model.counter.ResourceCounterMetric;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationMetric;
import org.json.JSONObject;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class JsonMetricSerializerTest extends AbstractMetricsTest {

    private static final String application = "testapp";

    private static final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();

    @Test
    public void testSerializesBusinessMetrics() {
        final EventMetric<PiiBusinessEventData> event = new EventMetric<>(
                ZonedDateTime.now(),
                application,
                MetricGroup.CLIENT,
                "created",
                defaultPiiBusinessEventData());

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("notes", "Some notes");
        expectedData.put("count", 1);
        expectedData.put("user_name", SecureObjectMapper.PII_MASK);

        assertThat(jsonObject, new JsonMetricMatcher(
                MetricGroup.CLIENT,
                application,
                "created",
                MetricType.EVENT,
                new JsonEventDataMatcher(expectedData)));
    }

    @Test
    public void testSerializesCounterMetrics() {
        final ResourceCounterMetric rcm = new ResourceCounterMetric(ZonedDateTime.now(),
                application,
                MetricGroup.CLIENT,
                "updated",
                new ResourceCounterEventData(10D));

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(rcm));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("count", 10D);

        assertThat(jsonObject, new JsonMetricMatcher(
                MetricGroup.CLIENT,
                application,
                "updated",
                MetricType.RESOURCE_COUNTER,
                new JsonEventDataMatcher(expectedData)));
    }

    @Test
    public void testSerializesCallTimeMetrics() {
        final HttpResourceCallMetric metric = new HttpResourceCallMetric(
                ZonedDateTime.now(),
                application,
                MetricGroup.CLIENT,
                "request",
                new HttpResourceCallEventData(Direction.OUTBOUND, 1000L, HttpResource.fromUrl("/test"), HttpAction.GET, Status.success(), trackingInfo()));

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(metric));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("direction", "OUTBOUND");
        expectedData.put("durationMillis", 1000);
        expectedData.put("resource", "/test");
        expectedData.put("action", "GET");
        expectedData.put("status", 0);

        assertThat(jsonObject, new JsonMetricMatcher(
                MetricGroup.CLIENT,
                application,
                "request",
                MetricType.RESOURCE_CALL,
                new JsonEventDataMatcher(expectedData)));
    }

    @Test
    public void testSerializesUtilizationMetrics() {
        final ResourceUtilizationMetric metric = new ResourceUtilizationMetric(
                ZonedDateTime.now(),
                application,
                MetricGroup.CLIENT,
                "thread-pool",
                new ResourceUtilizationData(0L, 1000L, 350L, 0.35D));

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(metric));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 0);
        expectedData.put("max", 1000);
        expectedData.put("curr", 350);
        expectedData.put("util", 0.35);

        assertThat(jsonObject, new JsonMetricMatcher(
                MetricGroup.CLIENT,
                application,
                "thread-pool",
                MetricType.RESOURCE_UTILIZATION,
                new JsonEventDataMatcher(expectedData)));
    }

}
