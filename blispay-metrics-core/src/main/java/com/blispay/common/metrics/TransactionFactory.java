package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.util.NameFormatter;

public final class TransactionFactory {

    private final EventType type = EventType.RESOURCE_CALL;

    private final String applicationId;
    private final EventEmitter emitter;

    private final EventGroup group;
    private final String name;
    private final Direction direction;
    private final Action action;
    private final Resource resource;

    private TransactionFactory(final String applicationId, final EventEmitter emitter,
                              final EventGroup group, final String name, final Direction direction, final Action action,
                              final Resource resource) {

        this.applicationId = applicationId;
        this.emitter = emitter;
        this.group = group;
        this.name = name;
        this.direction = direction;
        this.action = action;
        this.resource = resource;

    }

    /**
     * Create a new transaction with the currently configured state.
     *
     * @return Transaction instance.
     */
    public Transaction create() {

        return new Transaction(new EventRepository.Builder<>(applicationId, TransactionData.class, emitter)
                .inGroup(group)
                .ofType(type)
                .withName(name)
                .build())
                .inDirection(direction)
                .withAction(action)
                .onResource(resource);

    }

    public static class Builder {

        private final String applicationId;
        private final EventEmitter emitter;

        private EventGroup group;
        private String name;
        private Direction direction;
        private Action action;
        private Resource resource;

        public Builder(final String applicationId, final EventEmitter emitter) {
            this.applicationId = applicationId;
            this.emitter = emitter;
        }

        public Builder inGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withNameFromType(final Class<?> type) {
            this.name = NameFormatter.toEventName(type);
            return this;
        }

        public Builder inDirection(final Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder withAction(final Action action) {
            this.action = action;
            return this;
        }

        public Builder onResource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        public TransactionFactory build() {
            return new TransactionFactory(applicationId, emitter, group, name, direction, action, resource);
        }
    }

}
