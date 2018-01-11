package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.util.NameFormatter;

/**
 * Interface TransactionFactory.
 */
public interface TransactionFactory {

    /**
     * Create a new transaction with the currently configured state.
     *
     * @return Transaction instance.
     */
    Transaction create();

    /**
     * Create a new manual transaction with the currently configured state.
     *
     * @return Manual transaction instance.
     */
    ManualTransaction createManual();

    /**
     * Class Builder.
     */
    class Builder {

        private String applicationId;
        private EventEmitter emitter;

        private EventGroup group;
        private String name;
        private Direction direction;
        private Action action;
        private Resource resource;

        /**
         * Constructs Builder.
         *
         * @param applicationId applicationId.
         * @param emitter emitter.
         */
        public Builder(final String applicationId, final EventEmitter emitter) {
            this.applicationId = applicationId;
            this.emitter = emitter;
        }

        /**
         * Method inGroup.
         *
         * @param group group.
         * @return return value.
         */
        public Builder inGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        /**
         * Method withName.
         *
         * @param name name.
         * @return return value.
         */
        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Method withNameFromType.
         *
         * @param type type.
         * @return return value.
         */
        public Builder withNameFromType(final Class<?> type) {
            this.name = NameFormatter.toEventName(type);
            return this;
        }

        /**
         * Method inDirection.
         *
         * @param direction direction.
         * @return return value.
         */
        public Builder inDirection(final Direction direction) {
            this.direction = direction;
            return this;
        }

        /**
         * Method withAction.
         *
         * @param action action.
         * @return return value.
         */
        public Builder withAction(final Action action) {
            this.action = action;
            return this;
        }

        /**
         * Method onResource.
         *
         * @param resource resource.
         * @return return value.
         */
        public Builder onResource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        /**
         * Method build.
         *
         * @return return value.
         */
        public TransactionFactory build() {
            return new TransactionFactoryImpl(applicationId, emitter, group, name, direction, action, resource);
        }

    }

}
