package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.util.NameFormatter;

public class TransactionFactory {

    private final EventType type = EventType.RESOURCE_CALL;

    private final String applicationId;
    private final EventEmitter emitter;

    private EventGroup group;
    private String name;
    private Direction direction;
    private Action action;
    private Resource resource;

    public TransactionFactory(final String applicationId, final EventEmitter emitter) {
        this.applicationId = applicationId;
        this.emitter = emitter;
    }

    public TransactionFactory inGroup(final EventGroup group) {
        this.group = group;
        return this;
    }

    public TransactionFactory withName(final String name) {
        this.name = name;
        return this;
    }

    public TransactionFactory withNameFromType(final Class<?> type) {
        this.name = NameFormatter.toEventName(type);
        return this;
    }

    public TransactionFactory inDirection(final Direction direction) {
        this.direction = direction;
        return this;
    }

    public TransactionFactory withAction(final Action action) {
        this.action = action;
        return this;
    }

    public TransactionFactory onResource(final Resource resource) {
        this.resource = resource;
        return this;
    }

    public Transaction create() {
        return new Transaction(new EventRepository<>(TransactionData.class, applicationId, emitter)
                .inGroup(group)
                .ofType(type)
                .withName(name))
                .inDirection(direction)
                .withAction(action)
                .onResource(resource);

    }

}
