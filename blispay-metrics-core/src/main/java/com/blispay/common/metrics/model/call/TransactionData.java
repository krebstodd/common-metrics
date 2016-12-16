package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

/**
 * Class TransactionData.
 *
 * @param <R> Generic param type.
 * @param <A> Generic param type.
 */
public class TransactionData<R extends Resource, A extends Action> {

    @JsonProperty("direction")
    private final Direction direction;

    @JsonProperty("durationMillis")
    private final Long durationMillis;

    @JsonProperty("action")
    private final A action;

    @JsonProperty("resource")
    private final R resource;

    @JsonProperty("status")
    private final Integer status;

    /**
     * Immutable base resource call event data.
     *
     * @param direction Direction of resource call.
     * @param durationMillis Time in milliseconds for call to complete.
     * @param resource Resource being called.
     * @param action Action of call.
     * @param status Status of the response.
     */
    public TransactionData(final Direction direction, final Long durationMillis, final R resource, final A action, final Status status) {

        this.direction = direction;
        this.durationMillis = durationMillis;
        this.action = action;
        this.resource = resource;
        this.status = status.getValue();
    }

    /**
     * Method getDirection.
     *
     * @return return value.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Method getDurationMillis.
     *
     * @return return value.
     */
    public Long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Method getAction.
     *
     * @return return value.
     */
    public A getAction() {
        return action;
    }

    /**
     * Method getResource.
     *
     * @return return value.
     */
    public R getResource() {
        return resource;
    }

    /**
     * Method getStatus.
     *
     * @return return value.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Method builder.
     *
     * @return return value.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Class Builder.
     */
    public static class Builder {

        private Direction direction;
        private Long durationMillis;
        private Action action;
        private Resource resource;
        private Status status;

        /**
         * Constructs Builder.
         */
        public Builder() {}

        /**
         * Method direction.
         *
         * @param direction direction.
         * @return return value.
         */
        public Builder direction(final Direction direction) {
            this.direction = direction;
            return this;
        }

        /**
         * Method duration.
         *
         * @param duration duration.
         * @return return value.
         */
        public Builder duration(final Duration duration) {
            this.durationMillis = duration.toMillis();
            return this;
        }

        /**
         * Method action.
         *
         * @param action action.
         * @return return value.
         */
        public Builder action(final Action action) {
            this.action = action;
            return this;
        }

        /**
         * Method resource.
         *
         * @param resource resource.
         * @return return value.
         */
        public Builder resource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        /**
         * Method status.
         *
         * @param status status.
         * @return return value.
         */
        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        /**
         * Method build.
         *
         * @return return value.
         */
        public TransactionData build() {
            return new TransactionData<>(direction, durationMillis, resource, action, status);
        }

    }

}
