package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Resources indicate the entity on which an event is occurring. For instance, the resource in a transaction method
 * might be the API being called or what database table is being queried.
 */
public abstract class Resource {

    /**
     * Resource name value included in metric.
     *
     * @return return value.
     */
    @JsonValue
    public abstract String getValue();

    /**
     * Create a new resource instance.
     * @param resourceName Resource name.
     * @return Resource instanceo.
     */
    public static Resource withName(final String resourceName) {
        return new BasicResource(resourceName);
    }

}
