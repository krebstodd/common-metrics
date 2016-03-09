package com.blispay.common.metrics.model.call;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

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
    public TransactionData(final Direction direction, final Long durationMillis,
                           final R resource, final A action, final Status status) {

        this.direction = direction;
        this.durationMillis = durationMillis;
        this.action = action;
        this.resource = resource;
        this.status = status.getValue();
    }

    public Direction getDirection() {
        return direction;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public A getAction() {
        return action;
    }

    public R getResource() {
        return resource;
    }

    public Integer getStatus() {
        return status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Direction direction;
        private Long durationMillis;
        private Action action;
        private Resource resource;
        private Status status;

        public Builder() {

        }

        public Builder direction(final Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder duration(final Duration duration) {
            this.durationMillis = duration.toMillis();
            return this;
        }

        public Builder action(final Action action) {
            this.action = action;
            return this;
        }

        public Builder resource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }


        public TransactionData build() {
            return new TransactionData<>(direction, durationMillis, resource, action, status);
        }
    }

}
