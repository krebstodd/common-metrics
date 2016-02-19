package com.blispay.common.metrics.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonHelper {

    private JsonHelper() {

    }

    public static String toJson(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    public static <M> M fromJson(final String json, final Class<M> type) throws IOException {
        return new ObjectMapper().readValue(json, type);
    }

}
