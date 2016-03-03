package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.health.HealthCheckData;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.util.NameFormatter;

import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HealthMonitor implements SnapshotProvider {

    private static final EventType type = EventType.HEALTH_CHECK;

    private final String applicationId;
    private final EventGroup group;
    private final String name;
    private final Supplier<HealthCheckData> supplier;

    /**
     * Create a new health monitor.
     *
     * @param applicationId Application Id.
     * @param group Event group.
     * @param name Event name.
     * @param supplier Health supplier.
     */
    public HealthMonitor(final String applicationId,
                         final EventGroup group,
                         final String name,
                         final Supplier<HealthCheckData> supplier) {

        this.applicationId = applicationId;
        this.group = group;
        this.name = name;
        this.supplier = supplier;

    }

    @Override
    public EventModel<HealthCheckData> snapshot() {

        return new EventModel<>(ZonedDateTime.now(),
                this.applicationId,
                this.group,
                this.name,
                this.type,
                supplier.get());

    }

    public static class Builder {

        private final Consumer<HealthMonitor> gaugeRepository;
        private final String applicationId;

        private EventGroup group;
        private String name;
        private Supplier<HealthCheckData> supplier;

        public Builder(final String applicationId, final Consumer<HealthMonitor> gaugeRepository) {
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

        public Builder withSupplier(final Supplier<HealthCheckData> supplier) {
            this.supplier = supplier;
            return this;
        }

        /**
         * Register a new health monitory with the current configuration.
         *
         * @return The new health monitor. Used to clean up objects in the metric service if needed.
         */
        public HealthMonitor register() {
            final HealthMonitor gauge = new HealthMonitor(applicationId, group, name, supplier);
            gaugeRepository.accept(gauge);
            return gauge;
        }

    }

}
