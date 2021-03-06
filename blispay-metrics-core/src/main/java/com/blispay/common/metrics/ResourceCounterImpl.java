package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventHeader;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.TrackingInfo;
import com.blispay.common.metrics.model.counter.ResourceCountData;
import com.blispay.common.metrics.util.LocalMetricContext;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Class ResourceCounterImpl.
 */
public final class ResourceCounterImpl implements ResourceCounter {

    private final String applicationId;
    private final EventEmitter emitter;
    private final EventGroup group;
    private final String name;

    /**
     * Constructs ResourceCounterImpl.
     *
     * @param applicationId applicationId.
     * @param emitter emitter.
     * @param group group.
     * @param name name.
     */
    ResourceCounterImpl(final String applicationId, final EventEmitter emitter, final EventGroup group, final String name) {

        this.applicationId = applicationId;
        this.emitter = emitter;
        this.group = group;
        this.name = name;
    }

    /**
     * Method updateCount.
     *
     * @param count count.
     */
    public void updateCount(final Double count) {
        updateCount(ZonedDateTime.now(ZoneId.of("UTC")), count, null, LocalMetricContext.getTrackingInfo());
    }

    /**
     * Method updateCount.
     *
     * @param count count.
     * @param userData userData.
     */
    public void updateCount(final Double count, final Object userData) {
        updateCount(ZonedDateTime.now(ZoneId.of("UTC")), count, userData, LocalMetricContext.getTrackingInfo());
    }

    /**
     * Method updateCount.
     *
     * @param timestamp timestamp.
     * @param count count.
     * @param userData userData.
     * @param trackingInfo trackingInfo.
     */
    public void updateCount(final ZonedDateTime timestamp, final Double count, final Object userData, final TrackingInfo trackingInfo) {

        emitter.emit(new EventModel<>(createHeader(timestamp, trackingInfo), new ResourceCountData(count), userData));
    }

    private EventHeader createHeader(final ZonedDateTime timestamp, final TrackingInfo trackingInfo) {
        return EventHeader.builder().timestamp(timestamp).applicationId(applicationId).group(group).type(EventType.RESOURCE_COUNT).trackingInfo(trackingInfo).name(name).build();
    }

}
