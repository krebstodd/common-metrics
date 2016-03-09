package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.util.NameFormatter;

public interface TransactionFactory {

    /**
     * Create a new transaction with the currently configured state.
     *
     * @return Transaction instance.
     */
    Transaction create();

    class Builder {

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
            return new TransactionFactoryImpl(applicationId, emitter, group, name, direction, action, resource);
        }
    }
}
