package com.blispay.common.metrics.data;

import com.blispay.common.metrics.model.BaseMetricModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMetricSerializer implements MetricSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(JsonMetricSerializer.class);

    private ObjectMapper objectMapper;

    public JsonMetricSerializer() {
        this(new SecureObjectMapper());
    }

    public JsonMetricSerializer(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(final BaseMetricModel metric) {
        try {
            return objectMapper.writeValueAsString(metric);
        } catch (JsonProcessingException e) {
            LOG.error("Caught exception serializing json object", e);
            throw new IllegalArgumentException(e);
        }
    }
}
