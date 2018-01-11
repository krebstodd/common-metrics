package com.blispay.common.metrics.model.call;

/**
 * Basic resource impl.
 */
class BasicResource extends Resource {

    private final String resourceName;

    BasicResource(final String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getValue() {
        return this.resourceName;
    }

}
