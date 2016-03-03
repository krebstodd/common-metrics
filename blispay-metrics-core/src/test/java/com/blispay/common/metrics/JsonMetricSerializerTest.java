package com.blispay.common.metrics;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.SecureObjectMapper;
import com.blispay.common.metrics.matchers.JsonEventDataMatcher;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.counter.ResourceCountData;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import org.json.JSONObject;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertThat;

public class JsonMetricSerializerTest extends AbstractMetricsTest {

    private static final String application = "testapp";

    private static final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();

    @Test
    public void testSerializesBusinessMetrics() {

        final AtomicReference<EventModel> event = new AtomicReference<>();

        new EventRepository<>(PiiBusinessEventData.class, application, event::set)
                .inGroup(EventGroup.USER_DOMAIN)
                .withName("created")
                .ofType(EventType.BUSINESS_EVT)
                .save(defaultPiiBusinessEventData());

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("notes", "Some notes");
        expectedData.put("count", 1);
        expectedData.put("user_name", SecureObjectMapper.PII_MASK);

        assertThat(jsonObject, new JsonMetricMatcher(
                EventGroup.USER_DOMAIN,
                application,
                "created",
                EventType.BUSINESS_EVT,
                new JsonEventDataMatcher(expectedData)));

    }

    @Test
    public void testSerializesCounterMetrics() {
        final AtomicReference<EventModel> event = new AtomicReference<>();

        new EventRepository<>(ResourceCountData.class, application, event::set)
                .inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .withName("total-threads")
                .ofType(EventType.RESOURCE_COUNT)
                .save(new ResourceCountData(10D));

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("count", 10D);

        assertThat(jsonObject, new JsonMetricMatcher(
                EventGroup.RESOURCE_UTILIZATION_THREADS,
                application,
                "total-threads",
                EventType.RESOURCE_COUNT,
                new JsonEventDataMatcher(expectedData)));
    }

    @Test
    public void testSerializesCallTimeMetrics() {
        final AtomicReference<EventModel> event = new AtomicReference<>();

        new EventRepository<>(TransactionData.class, application, event::set)
                .inGroup(EventGroup.CLIENT_HTTP)
                .withName("some-request")
                .ofType(EventType.RESOURCE_CALL)
                .save(TransactionData.builder()
                        .direction(Direction.INBOUND)
                        .duration(Duration.ofSeconds(1))
                        .action(HttpAction.GET)
                        .resource(HttpResource.fromUrl("http://blispay.com"))
                        .status(Status.success())
                        .message("My message.")
                        .trackingInfo(trackingInfo())
                        .customField("myField", "myValue")
                        .build());

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("direction", "INBOUND");
        expectedData.put("durationMillis", 1000);
        expectedData.put("resource", "http://blispay.com");
        expectedData.put("action", "GET");
        expectedData.put("status", 0);
        expectedData.put("myField", "myValue");

        assertThat(jsonObject, new JsonMetricMatcher(
                EventGroup.CLIENT_HTTP,
                application,
                "some-request",
                EventType.RESOURCE_CALL,
                new JsonEventDataMatcher(expectedData)));
    }

    @Test
    public void testSerializesUtilizationMetrics() {

        final AtomicReference<EventModel> event = new AtomicReference<>();

        new EventRepository<>(ResourceUtilizationData.class, application, event::set)
                .inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS)
                .ofType(EventType.RESOURCE_UTILIZATION)
                .withName("thread-pool")
                .save(new ResourceUtilizationData(0L, 1000L, 350L, 0.35D));

        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 0);
        expectedData.put("max", 1000);
        expectedData.put("curr", 350);
        expectedData.put("util", 0.35);

        assertThat(jsonObject, new JsonMetricMatcher(
                EventGroup.RESOURCE_UTILIZATION_THREADS,
                application,
                "thread-pool",
                EventType.RESOURCE_UTILIZATION,
                new JsonEventDataMatcher(expectedData)));
    }

}
