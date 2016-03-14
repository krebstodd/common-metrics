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

    void updateCount(final Double count);

    void updateCount(final Double count, final Object userData);

    void updateCount(final ZonedDateTime timestamp,
                            final Double count,
                            final Object userData,
                            final TrackingInfo trackingInfo);

    class Builder {

        private final String applicationId;
        private final EventEmitter emitter;

        private EventGroup group;
        private String name;

        public Builder(final String applicationId, final EventEmitter emitter) {
            this.applicationId = applicationId;
            this.emitter = emitter;
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

        public ResourceCounter build() {
            return new ResourceCounterImpl(applicationId, emitter, group, name);
        }
    }

}
