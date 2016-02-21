package com.blispay.common.metrics.data;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class SecureJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    public static final String DEFAULT_FILTER_ID = PiiBeanPropertyFilter.NAME;

    @Override
    public Object findFilterId(final Annotated ac) {

        // First, let's consider @JsonFilter by calling superclass to allow devs to override PII filter if needed.
        Object id = super.findFilterId(ac);

        // If not found use the default PII filter
        if (id == null) {
            id = DEFAULT_FILTER_ID;
        }

        return id;
    }

}
