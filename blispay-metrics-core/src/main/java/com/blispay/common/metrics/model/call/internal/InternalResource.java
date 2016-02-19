package com.blispay.common.metrics.model.call.internal;

import com.blispay.common.metrics.model.call.Resource;

public class InternalResource extends Resource {

    private final String resource;

    private InternalResource(final String resource) {
        this.resource = resource;
    }

    @Override
    public String getValue() {
        return resource;
    }

    public InternalResource fromClass(final Class resource) {
        return new InternalResource(resource.getName());
    }
}
