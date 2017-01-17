package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NameFormatter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class UtilizationGauge.
 */
public class UtilizationGauge implements SnapshotProvider {

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
    public UtilizationGauge(final String applicationId, final EventGroup group, final String name, final Supplier<ResourceUtilizationData> supplier) {

        this.applicationId = applicationId;
        this.group = group;
        this.name = name;
        this.supplier = supplier;

    }

    @Override
    public EventModel<ResourceUtilizationData, Void> snapshot() {
        return new EventModel<>(createHeader(), supplier.get(), null);
    }

    @Override
    public String id() {
        return name;
    }

    @Override
    public String description() {
        return String.format("Utilization gauge: name=[%s], group=[%s]", name, group);
    }

    private EventHeader createHeader() {
        return EventHeader.builder()
                          .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                          .applicationId(applicationId)
                          .group(group)
                          .type(EventType.RESOURCE_UTILIZATION)
                          .trackingInfo(LocalMetricContext.getTrackingInfo())
                          .name(name)
                          .build();
    }

    /**
     * Class Builder.
     */
    public static class Builder {

        private final Consumer<UtilizationGauge> gaugeRepository;
        private final String applicationId;

        private EventGroup group;
        private String name;

        /**
         * Constructs Builder.
         *
         * @param applicationId applicationId.
         * @param gaugeRepository gaugeRepository.
         */
        public Builder(final String applicationId, final Consumer<UtilizationGauge> gaugeRepository) {
            this.applicationId = applicationId;
            this.gaugeRepository = gaugeRepository;
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
