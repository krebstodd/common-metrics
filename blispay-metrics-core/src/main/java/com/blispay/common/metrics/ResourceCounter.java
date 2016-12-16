package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.util.NameFormatter;

import java.time.ZonedDateTime;

/**
 * Resource Counter interface.
 * @author Dustin Conrad
 */
public interface ResourceCounter {

    /**
     * Method updateCount.
     *
     * @param count count.
     */
    void updateCount(Double count);

    /**
     * Method updateCount.
     *
     * @param count count.
     * @param userData userData.
     */
    void updateCount(Double count, Object userData);

    /**
     * Method updateCount.
     *
     * @param timestamp timestamp.
     * @param count count.
     * @param userData userData.
     * @param trackingInfo trackingInfo.
     */
    void updateCount(ZonedDateTime timestamp, Double count, Object userData, TrackingInfo trackingInfo);

    /**
     * Class Builder.
     */
    class Builder {

        private String applicationId;
        private EventEmitter emitter;

        private EventGroup group;
        private String name;

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
         * Method build.
         *
         * @return return value.
         */
        public ResourceCounter build() {
            return new ResourceCounterImpl(applicationId, emitter, group, name);
        }

    }

}
