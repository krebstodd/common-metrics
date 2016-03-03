package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.NoOpEventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NameFormatter;
import com.blispay.common.metrics.util.TrackingInfoAware;
import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;

public class EventRepository<D> {

    private final String application;
    private final EventEmitter eventEmitter;
    private final Class<D> hint;

    private ZonedDateTime timestamp = ZonedDateTime.now();
    private EventGroup group;
    private String name;
    private EventType type;

    public EventRepository(final Class<D> hint) {
        this(hint, null, new NoOpEventEmitter());
    }

    public EventRepository(final Class<D> hint, final String applicationId) {
        this(hint, applicationId, new NoOpEventEmitter());
    }

    /**
     * Create a new event repository with the providved base information.
     *
     * @param hint Hint at what type of payload to expect.
     * @param applicationId The application name of the currently running process.
     * @param emitter An event emitter instance used to save events once created.
     */
    public EventRepository(final Class<D> hint, final String applicationId, final EventEmitter emitter) {
        this.hint = hint;
        this.eventEmitter = emitter;
        this.application = applicationId;
    }

    public EventRepository<D> inGroup(final EventGroup group) {
        this.group = group;
        return this;
    }

    public EventRepository<D> withName(final String name) {
        this.name = name;
        return this;
    }

    public EventRepository<D> withNameFromType(final Class<?> type) {
        this.name = NameFormatter.toEventName(type);
        return this;
    }

    public EventRepository<D> ofType(final EventType type) {
        this.type = type;
        return this;
    }

    public EventRepository<D> withTimestamp(final ZonedDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Class<D> getHint() {
        return hint;
    }

    /**
     * Save a new event with the current repository configuration.
     *
     * @param eventData Event data payload.
     * @return The newly saved model.
     */
    public EventModel<D> save(final D eventData) {

        Preconditions.checkNotNull(timestamp, "Timestamp required.");
        Preconditions.checkNotNull(application, "Application id required.");
        Preconditions.checkNotNull(group, "Event group required.");
        Preconditions.checkNotNull(name, "Event name required.");
        Preconditions.checkNotNull(type, "Event type required.");
        Preconditions.checkNotNull(eventData);

        if (eventData instanceof TrackingInfoAware) {
            ((TrackingInfoAware) eventData).setTrackingInfo(LocalMetricContext.getTrackingInfo());
        }

        final EventModel<D> event = new EventModel<>(timestamp, application, group, name, type, eventData);
        eventEmitter.emit(event);
        return event;

    }

}
