package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.status.StatusData;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NameFormatter;
import com.google.common.base.Preconditions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StateMonitor implements SnapshotProvider {

    private final String applicationId;
    private final EventGroup group;
    private final String name;
    private final Supplier<StatusData> supplier;

    /**
     * Create a new health monitor.
     *
     * @param applicationId Application Id.
     * @param group Event group.
     * @param name Event name.
     * @param supplier Health supplier.
     */
    public StateMonitor(final String applicationId,
                        final EventGroup group,
                        final String name,
                        final Supplier<StatusData> supplier) {

        this.applicationId = applicationId;
        this.group = group;
        this.name = name;
        this.supplier = supplier;

    }

    @Override
    public EventModel<StatusData, Void> snapshot() {
        return new EventModel<>(createHeader(), supplier.get());
    }

    private EventHeader createHeader() {
        return EventHeader.builder()
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .applicationId(applicationId)
                .group(group)
                .type(EventType.STATUS)
                .trackingInfo(LocalMetricContext.getTrackingInfo())
                .name(name)
                .build();
    }

    public static class Builder {

        private final Consumer<StateMonitor> gaugeRepository;
        private final String applicationId;

        private EventGroup group;
        private String name;
        private Supplier<StatusData> supplier;

        public Builder(final String applicationId, final Consumer<StateMonitor> gaugeRepository) {
            this.applicationId = applicationId;
            this.gaugeRepository = gaugeRepository;
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

        public Builder withSupplier(final Supplier<StatusData> supplier) {
            this.supplier = supplier;
            return this;
        }

        /**
         * Register a new health monitory with the current configuration.
         *
         * @return The new health monitor. Used to clean up objects in the metric service if needed.
         */
        public StateMonitor register() {

            Preconditions.checkNotNull(applicationId);
            Preconditions.checkNotNull(group);
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(supplier);

            final StateMonitor gauge = new StateMonitor(applicationId, group, name, supplier);
            gaugeRepository.accept(gauge);
            return gauge;
        }

    }

}