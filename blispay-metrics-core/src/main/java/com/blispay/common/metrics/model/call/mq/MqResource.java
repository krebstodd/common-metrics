package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.call.Resource;

/**
 * Class MqResource.
 */
public final class MqResource extends Resource {

    private final String resourceName;

    /**
     * Constructs MqResource.
     *
     * @param name name.
     */
    private MqResource(final String name) {
        this.resourceName = name;
    }

    @Override
    public String getValue() {
        return resourceName;
    }

    /**
     * Method fromQueueName.
     *
     * @param queueName queueName.
     * @return return value.
     */
    public static MqResource fromQueueName(final String queueName) {
        return new MqResource(queueName);
    }

}
