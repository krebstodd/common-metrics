package com.blispay.common.metrics.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

import java.io.IOException;
import java.util.List;

public class SecureObjectMapper extends ObjectMapper {

    public static final String PII_MASK = "*** PII DETECTED ***";

    private final ObjectMapper mapper;

    public SecureObjectMapper() {
        this(new PiiFieldPredicate());
    }

    public SecureObjectMapper(final PiiFieldPredicate piiPredicate) {

        this.mapper = new ObjectMapper();

        final SerializerFactory serializerFactory = BeanSerializerFactory
                .instance
                .withSerializerModifier(new SecureSerializerModifier(piiPredicate));

        this.mapper.setSerializerFactory(serializerFactory);

    }

    @Override
    public String writeValueAsString(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public class SecureSerializerModifier extends BeanSerializerModifier {

        private final PiiFieldPredicate piiPredicate;

        public SecureSerializerModifier(final PiiFieldPredicate piiPredicate) {
            this.piiPredicate = piiPredicate;
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {

            for (int i = 0; i < beanProperties.size(); i++) {
                BeanPropertyWriter beanPropertyWriter = beanProperties.get(i);

                if (piiPredicate.test(beanPropertyWriter.getName())) {
                    beanPropertyWriter.assignSerializer(new PiiSerializer());
                }

            }

            return beanProperties;
        }

    }

    private static class PiiSerializer extends JsonSerializer {

        @Override
        public void serialize(final Object pojo, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeString(SecureObjectMapper.PII_MASK);
        }

    }

}
