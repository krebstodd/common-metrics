package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.google.common.base.Preconditions;

/**
 * Class EventFactory.
 *
 * @param <U> Generic param type.
 */
public class EventFactory<U> {

    private final Class<U> userDataHint;
    private final String applicationId;
    private final EventEmitter emitter;
    private final EventGroup group;
    private final String name;

    /**
     * Constructs EventFactory.
     *
     * @param userDataHint userDataHint.
     * @param applicationId applicationId.
     * @param emitter emitter.
     * @param group group.
     * @param name name.
     */
    protected EventFactory(final Class<U> userDataHint, final String applicationId, final EventEmitter emitter, final EventGroup group, final String name) {

        this.userDataHint = userDataHint;
        this.applicationId = applicationId;
        this.emitter = emitter;
        this.group = group;
        this.name = name;
    }

    /**
     * Method create.
     *
     * @return return value.
     */
    public Event<U> create() {
        return new Event<>(this.userDataHint, this.emitter, this.applicationId, this.group, this.name);
    }

    /**
     * Method save.
     *
     * @param obj obj.
     * @return return value.
     */
    public EventModel<Void, U> save(final U obj) {
        return create().save(obj);
    }

    /**
     * Class Builder.
     *
     * @param <U> Generic param type.
     */
    public static class Builder<U> {

        private final Class<U> hint;
        private final String applicationId;
        private final EventEmitter emitter;

        private EventGroup group;
        private String name;

        /**
         * Create a new event factory builder.
         *
         * @param hint User data type.
         * @param applicationId App id.
         * @param emitter Event emitter.
         */
        public Builder(final Class<U> hint, final String applicationId, final EventEmitter emitter) {

            this.hint = hint;
            this.applicationId = applicationId;
            this.emitter = emitter;
        }

        /**
         * Method withName.
         *
         * @param name name.
         * @return return value.
         */
        public Builder<U> withName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Method inGroup.
         *
         * @param group group.
         * @return return value.
         */
        public Builder<U> inGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        /**
         * Build a new event factory.
         * @return new event factory.
         */
        public EventFactory<U> build() {

            Preconditions.checkNotNull(this.group);

            return new EventFactory<>(hint, applicationId, emitter, group, name);

        }

    }

}
