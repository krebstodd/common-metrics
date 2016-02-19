package com.blispay.common.metrics.model.call.mq;

import com.blispay.common.metrics.model.call.Resource;

public class MqResource extends Resource {

    private final String resourceName;

    private MqResource(final String name) {
        this.resourceName = name;
    }

    @Override
    public String getValue() {
        return resourceName;
    }

    public static MqResource fromQueueName(final String queueName) {
        return new MqResource(queueName);
    }

}