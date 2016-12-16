package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.google.common.base.Preconditions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class Gauge.
 *
 * @param <U> Generic param type.
 */
public class Gauge<U> implements SnapshotProvider {

    private final Class<U> userDataHint;
    private final String applicationId;
    private final EventGroup group;
    private final String name;

    private final Supplier<U> supplier;

    /**
     * Constructs Gauge.
     *
     * @param userDataHint userDataHint.
     * @param applicationId applicationId.
     * @param group group.
     * @param name name.
     * @param supplier supplier.
     */
    protected Gauge(final Class<U> userDataHint, final String applicationId, final EventGroup group, final String name, final Supplier<U> supplier) {

        this.userDataHint = userDataHint;
        this.applicationId = applicationId;
        this.group = group;
        this.name = name;
        this.supplier = supplier;
    }

    @Override
    public EventModel<Void, U> snapshot() {

        final EventHeader header = EventHeader.builder().applicationId(applicationId).group(group).name(name).type(EventType.EVENT).timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();

        return new EventModel<>(header, null, supplier.get());

    }

    /**
     * Method getUserDataHint.
     *
     * @return return value.
     */
    public Class<U> getUserDataHint() {
        return userDataHint;
    }

    /**
     * Class Builder.
     *
     * @param <U> Generic param type.
     */
    public static class Builder<U> {

        private final Class<U> hint;
        private final String applicationId;
        private final Consumer<Gauge<U>> registerCallback;

        private EventGroup group;
        private String name;

        /**
         * Create a new event factory builder.
         *
         * @param hint User data type.
         * @param applicationId App id.
         * @param registerCallback callback to register the gauge with the metric service.
         */
        public Builder(final Class<U> hint, final String applicationId, final Consumer<Gauge<U>> registerCallback) {

            this.hint = hint;
            this.applicationId = applicationId;
            this.registerCallback = registerCallback;
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
         * Build a new gauge.
         * @param supplier state supplier.
         * @return new gauge.
         */
        public Gauge<U> register(final Supplier<U> supplier) {

            Preconditions.checkNotNull(this.group);

            final Gauge<U> gauge = new Gauge<>(hint, applicationId, group, name, supplier);
            registerCallback.accept(gauge);
            return gauge;
        }

    }

}
