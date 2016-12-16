package com.blispay.common.metrics;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.data.SecureObjectMapper;
import com.blispay.common.metrics.matchers.JsonEventDataMatcher;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.transaction.TransactionFactory;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Class JsonMetricSerializerTest.
 */
public class JsonMetricSerializerTest extends AbstractMetricsTest {

    private static final String APPLICATION = "testapp";

    /**
     * Method testSerializesBusinessMetrics.
     *
     */
    @Test
    public void testSerializesBusinessMetrics() {

        final AtomicReference<EventModel> event = new AtomicReference<>();

        final EventFactory<PiiBusinessEventData> factory = new EventFactory.Builder<>(PiiBusinessEventData.class, APPLICATION, event::set).inGroup(EventGroup.USER_DOMAIN).withName("created").build();

        factory.save(defaultPiiBusinessEventData());

        final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();
        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("notes", "Some notes");
        expectedData.put("count", 1);
        expectedData.put("user_name", SecureObjectMapper.PII_MASK);

        assertThat(jsonObject, new JsonMetricMatcher(EventGroup.USER_DOMAIN, APPLICATION, "created", EventType.EVENT, Matchers.nullValue(), new JsonEventDataMatcher(expectedData)));

    }

    /**
     * Method testSerializesCounterMetrics.
     *
     */
    @Test
    public void testSerializesCounterMetrics() {
        final AtomicReference<EventModel> event = new AtomicReference<>();

        new ResourceCounter.Builder(APPLICATION, event::set).inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS).withName("total-threads").build().updateCount(10D);

        final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();
        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("count", 10D);

        assertThat(jsonObject,
                   new JsonMetricMatcher(EventGroup.RESOURCE_UTILIZATION_THREADS,
                                         APPLICATION,
                                         "total-threads",
                                         EventType.RESOURCE_COUNT,
                                         new JsonEventDataMatcher(expectedData),
                                         Matchers.nullValue()));
    }

    /**
     * Method testSerializesCallTimeMetrics.
     *
     */
    @Test
    public void testSerializesCallTimeMetrics() {
        final AtomicReference<EventModel> event = new AtomicReference<>();

        new TransactionFactory.Builder(APPLICATION, event::set).inGroup(EventGroup.CLIENT_HTTP_DDS)
                                                               .withName("some-request")
                                                               .build()
                                                               .create()
                                                               .inDirection(Direction.INBOUND)
                                                               .withAction(HttpAction.GET)
                                                               .onResource(HttpResource.fromUrl("http://blispay.com"))
                                                               .start()
                                                               .stop(Status.success());

        final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();
        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event.get()));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("direction", "INBOUND");
        expectedData.put("resource", "http://blispay.com");
        expectedData.put("action", "GET");
        expectedData.put("status", 0);

        assertThat(jsonObject, new JsonMetricMatcher(EventGroup.CLIENT_HTTP_DDS, APPLICATION, "some-request", EventType.TRANSACTION, new JsonEventDataMatcher(expectedData), Matchers.nullValue()));

        assertNotNull(jsonObject.getJSONObject("data").getDouble("durationMillis"));
    }

    /**
     * Method testSerializesUtilizationMetrics.
     *
     */
    @Test
    public void testSerializesUtilizationMetrics() {

        final EventModel event = new UtilizationGauge.Builder(APPLICATION, gauge -> { }
        ).inGroup(EventGroup.RESOURCE_UTILIZATION_THREADS).withName("thread-pool").register(() -> new ResourceUtilizationData(0L, 1000L, 350L, 0.35D)).snapshot();

        final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();
        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event));

        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 0);
        expectedData.put("max", 1000);
        expectedData.put("curr", 350);
        expectedData.put("util", 0.35);

        assertThat(jsonObject,
                   new JsonMetricMatcher(EventGroup.RESOURCE_UTILIZATION_THREADS,
                                         APPLICATION,
                                         "thread-pool",
                                         EventType.RESOURCE_UTILIZATION,
                                         new JsonEventDataMatcher(expectedData),
                                         Matchers.nullValue()));
    }

}
