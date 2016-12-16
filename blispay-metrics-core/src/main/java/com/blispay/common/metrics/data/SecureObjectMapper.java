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

/**
 * Class SecureObjectMapper.
 */
public class SecureObjectMapper extends ObjectMapper {

    /**
     * String used to mask pii data fields.
     */
    public static final String PII_MASK = "*** PII DETECTED ***";

    private final ObjectMapper mapper;

    /**
     * Constructs SecureObjectMapper.
     */
    public SecureObjectMapper() {
        this(new PiiFieldPredicate());
    }

    /**
     * Create a new secure object mapper. Strips out any fields that are likely to contain PII.
     *
     * @param piiPredicate Predicate testing whether a field key is likely to contain a PII value.
     */
    public SecureObjectMapper(final PiiFieldPredicate piiPredicate) {

        this.mapper = new ObjectMapper();

        final SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(new SecureSerializerModifier(piiPredicate));

        this.mapper.setSerializerFactory(serializerFactory);

    }

    @Override
    public String writeValueAsString(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    /**
     * Class SecureSerializerModifier.
     */
    private static final class SecureSerializerModifier extends BeanSerializerModifier {

        private final PiiFieldPredicate piiPredicate;

        /**
         * Constructs SecureSerializerModifier.
         *
         * @param piiPredicate piiPredicate.
         */
        private SecureSerializerModifier(final PiiFieldPredicate piiPredicate) {
            this.piiPredicate = piiPredicate;
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {

            for (int i = 0; i < beanProperties.size(); i++) {

                final BeanPropertyWriter beanPropertyWriter = beanProperties.get(i);

                if (piiPredicate.test(beanPropertyWriter.getName())) {
                    beanPropertyWriter.assignSerializer(new PiiSerializer());
                }

            }

            return beanProperties;
        }

    }

    /**
     * Class PiiSerializer.
     */
    private static class PiiSerializer extends JsonSerializer {

        @Override
        public void serialize(final Object pojo, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(SecureObjectMapper.PII_MASK);
        }

    }

}
