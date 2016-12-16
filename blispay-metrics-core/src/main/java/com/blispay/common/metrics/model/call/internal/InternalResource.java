package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.call.Resource;

/**
 * Class InternalResource.
 */
public final class InternalResource extends Resource {

    private final String resource;

    /**
     * Constructs InternalResource.
     *
     * @param resource resource.
     */
    private InternalResource(final String resource) {
        this.resource = resource;
    }

    @Override
    public String getValue() {
        return resource;
    }

    /**
     * Method fromClass.
     *
     * @param resource resource.
     * @return return value.
     */
    public static InternalResource fromClass(final Class resource) {
        return new InternalResource(resource.getName());
    }

}
