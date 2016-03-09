package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.util.LocalMetricContext;
import com.blispay.common.metrics.util.NameFormatter;
import com.google.common.base.Preconditions;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class Event<U> {

    private final EventEmitter emitter;
    private final String appId;
    private final EventGroup group;
    private final Class<U> hint;

    private String name;

    Event(final Class<U> hint,
          final EventEmitter emitter,
          final String appId,
          final EventGroup group) {

        this(hint, emitter, appId, group, null);
    }


    Event(final Class<U> hint,
          final EventEmitter emitter,
          final String appId,
          final EventGroup group,
          final String name) {

        this.hint = hint;
        this.emitter = emitter;
        this.appId = appId;
        this.group = group;
        this.name = name;
    }

    public Event<U> name(final String name) {
        this.name = name;
        return this;
    }

    public Event<U> name(final Class<?> nameClazz) {
        this.name = NameFormatter.toEventName(nameClazz);
        return this;
    }

    public Class<U> getHint() {
        return hint;
    }

    /**
     * Save a new event.
     * @param userData User data.
     * @return New immutable event model.
     */
    public EventModel<Void, U> save(final U userData) {

        Preconditions.checkNotNull(userData);
        Preconditions.checkNotNull(name);

        final EventModel<Void, U> model = new EventModel<>(createHeader(), null, userData);
        emitter.emit(model);
        return model;

    }

    private EventHeader createHeader() {
        return EventHeader.builder()
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .applicationId(appId)
                .group(group)
                .type(EventType.EVENT)
                .trackingInfo(LocalMetricContext.getTrackingInfo())
                .name(name)
                .build();
    }

}
