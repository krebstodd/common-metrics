package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.model.call.Resource;

/**
 * Metric resource type representing the target of an AOP proxy.
 */
public final class AopResource extends Resource {

    private final String value;

    private AopResource(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static AopResource withName(final String resourceName) {
        return new AopResource(resourceName);
    }
}
