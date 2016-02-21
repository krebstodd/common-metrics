package com.blispay.common.metrics;

import com.blispay.common.metrics.data.JsonMetricSerializer;
import com.blispay.common.metrics.matchers.JsonMetricMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.business.EventMetric;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertThat;

public class JsonMetricSerializerTest extends AbstractMetricsTest {

    private static final JsonMetricSerializer jsonSerializer = new JsonMetricSerializer();

    @Test
    public void testSerializesBusinessMetrics() {
        final EventMetric<PiiBusinessEventData> event = new EventMetric<>(
                ZonedDateTime.now(),
                MetricGroup.GENERIC,
                "created",
                trackingInfo(),
                defaultPiiBusinessEventData());

        System.out.println(jsonSerializer.serialize(event));
        final JSONObject jsonObject = new JSONObject(jsonSerializer.serialize(event));

        assertThat(jsonObject, new JsonMetricMatcher(
                MetricGroup.GENERIC,
                "created",
                MetricType.EVENT,
                Matchers.notNullValue()));
    }

    @Test
    public void testSerializesCounterMetrics() {

    }

    @Test
    public void testSerializesCallTimeMetrics() {

    }

    @Test
    public void testSerializesUtilizationMetrics() {

    }

    @Test
    public void testRemovesPiiByDefault() {

    }

    @Test
    public void testPermitsCustomFilters() {

    }



}
