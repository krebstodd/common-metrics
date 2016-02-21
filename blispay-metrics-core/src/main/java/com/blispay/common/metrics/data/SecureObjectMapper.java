package com.blispay.common.metrics.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;

public class SecureObjectMapper extends ObjectMapper {

    private final ObjectMapper mapper;

    public SecureObjectMapper() {
        this(new PiiBeanPropertyFilter());
    }

    public SecureObjectMapper(final PiiBeanPropertyFilter piiFilter) {
        final FilterProvider piiFilterProvider = new SimpleFilterProvider().addFilter(PiiBeanPropertyFilter.NAME, piiFilter);

        this.mapper = new ObjectMapper();
        this.mapper.setAnnotationIntrospector(new SecureJacksonAnnotationIntrospector());
        this.mapper.setFilterProvider(piiFilterProvider);
    }

    @Override
    public String writeValueAsString(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public <M> M fromJson(final String json, final Class<M> type) throws IOException {
        return mapper.readValue(json, type);
    }

}
