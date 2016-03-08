package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NotYetStartedException;
import com.blispay.common.metrics.util.TrackingInfoAware;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Optional;

public final class EventRepository<D> {

    private static final Logger LOG = LoggerFactory.getLogger(EventRepository.class);

    private final String application;
    private final EventEmitter eventEmitter;
    private final Class<D> hint;
    private final EventGroup group;
    private final String name;
    private final EventType type;

    private EventRepository(final Class<D> hint, final String applicationId, final EventEmitter emitter,
                           final EventGroup group, final String name, final EventType type) {
        this.hint = hint;
        this.eventEmitter = emitter;
        this.application = applicationId;
        this.group = group;
        this.name = name;
        this.type = type;
    }

    public Class<D> getHint() {
        return hint;
    }

    public EventModel<D> save(final D eventData) {
        return save(this.name, ZonedDateTime.now(), eventData);
    }

    public EventModel<D> save(final String eventNameOverride, final D eventData) {
        return save(eventNameOverride, ZonedDateTime.now(), eventData);
    }

    public EventModel<D> save(final ZonedDateTime timestamp, final D eventData) {
        return save(name, timestamp, eventData);
    }

    /**
     * Save a new event with the current repository configuration.
     *
     * @param nameOverride custom event name override.
     * @param timestamp custom time stamp for event.
     * @param eventData Event data payload.
     * @return The newly saved model.
     */
    public EventModel<D> save(final String nameOverride, final ZonedDateTime timestamp, final D eventData) {

        if (eventData instanceof TrackingInfoAware) {
            ((TrackingInfoAware) eventData).setTrackingInfo(LocalMetricContext.getTrackingInfo());
        }

        final EventModel<D> event
                = new EventModel<>(timestamp, application, group, Optional.ofNullable(nameOverride).orElse(name), type, eventData);

        try {

            eventEmitter.emit(event);

        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {
            LOG.error("Caught exception saving event...");

            if (ex instanceof NotYetStartedException) {
                throw ex;
            }
        }
        // CHECK_ON: IllegalCatch

        return event;

    }

    public static class Builder<D> {

        private final EventEmitter emitter;
        private final String applicationId;
        private final Class<D> hint;

        private EventGroup group;
        private String name;
        private EventType type;

        /**
         * Create an event repository builder.
         * @param applicationId application id
         * @param hint payload hint
         * @param eventEmitter event emitter
         */
        public Builder(final String applicationId, final Class<D> hint, final EventEmitter eventEmitter) {
            this.applicationId = applicationId;
            this.hint = hint;
            this.emitter = eventEmitter;
        }

        public Builder<D> inGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        public Builder<D> withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder<D> ofType(final EventType type) {
            this.type = type;
            return this;
        }

        public EventModel<D> save(final D data) {
            return build().save(data);
        }

        /**
         * Build a new event repository instance.
         * @return New instance.
         */
        public EventRepository<D> build() {

            Preconditions.checkNotNull(applicationId, "Application id required.");
            Preconditions.checkNotNull(group, "Event group required.");
            Preconditions.checkNotNull(type, "Event type required.");

            return new EventRepository<>(hint, applicationId, emitter, group, name, type);

        }
    }

}
