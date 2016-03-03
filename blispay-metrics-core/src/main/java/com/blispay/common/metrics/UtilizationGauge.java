package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.util.NameFormatter;

import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UtilizationGauge implements SnapshotProvider {

    private final EventType type = EventType.RESOURCE_UTILIZATION;

    private final String applicationId;
    private final EventGroup group;
    private final String name;
    private final Supplier<ResourceUtilizationData> supplier;

    /**
     * Create a new resource utilization gauge.
     *
     * @param applicationId identifier of current process.
     * @param group Group the gauge belongs in.
     * @param name Name of the gauge.
     * @param supplier Supplier of gauge state.
     */
    public UtilizationGauge(final String applicationId,
                            final EventGroup group,
                            final String name,
                            final Supplier<ResourceUtilizationData> supplier) {

        this.applicationId = applicationId;
        this.group = group;
        this.name = name;
        this.supplier = supplier;

    }

    @Override
    public EventModel<ResourceUtilizationData> snapshot() {
        return new EventModel<>(ZonedDateTime.now(),
                this.applicationId,
                this.group,
                this.name,
                this.type,
                supplier.get());
    }

    public static class Factory {

        private final Consumer<UtilizationGauge> gaugeRepository;
        private final String applicationId;

        private EventGroup group;
        private String name;

        public Factory(final String applicationId, final Consumer<UtilizationGauge> gaugeRepository) {
            this.applicationId = applicationId;
            this.gaugeRepository = gaugeRepository;
        }

        public Factory inGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        public Factory withName(final String name) {
            this.name = name;
            return this;
        }

        public Factory withNameFromType(final Class<?> type) {
            this.name = NameFormatter.toEventName(type);
            return this;
        }

        /**
         * Register the currently configured gauge with the metric service for periodic logging.
         *
         * @param supplier Supplier of gauge data.
         * @return The newly registered gauge.
         */
        public UtilizationGauge register(final Supplier<ResourceUtilizationData> supplier) {
            final UtilizationGauge gauge = new UtilizationGauge(applicationId, group, name, supplier);
            gaugeRepository.accept(gauge);
            return gauge;
        }

    }
}
