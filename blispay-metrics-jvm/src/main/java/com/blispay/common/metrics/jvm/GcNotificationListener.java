package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.EventFactory;
import com.sun.management.GarbageCollectionNotificationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.MemoryUsage;
import java.util.Map;

public class GcNotificationListener implements NotificationListener {

    private static final Logger LOG = LoggerFactory.getLogger(GcNotificationListener.class);

    private final EventFactory<GcEventData> gcEventFactory;

    public GcNotificationListener(final EventFactory<GcEventData> gcEventRepo) {
        this.gcEventFactory = gcEventRepo;
    }

    @Override
    public void handleNotification(final Notification notification, final Object handback) {

        LOG.debug("Received jmx notification.");

        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {

            LOG.debug("Detected garbage collection notification.");

            try {

                final CompositeData cData = (CompositeData) notification.getUserData();
                final GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo.from(cData);

                final Map<String, MemoryUsage> preGc = gcInfo.getGcInfo().getMemoryUsageBeforeGc();
                final Map<String, MemoryUsage> postGc = gcInfo.getGcInfo().getMemoryUsageAfterGc();

                for (String key : preGc.keySet()) {
                    LOG.debug("Detected pre-gc memory usage for region [{}]", key);
                }

                final GcEventData.Builder builder = new GcEventData.Builder()
                        .action(gcInfo.getGcAction())
                        .cause(gcInfo.getGcCause())
                        .name(gcInfo.getGcName())
                        .durationMillis(gcInfo.getGcInfo().getDuration())
                        .startTime(gcInfo.getGcInfo().getStartTime())
                        .endTime(gcInfo.getGcInfo().getEndTime());

                preGc.forEach((poolName, usage) -> builder.preGcFreeMem(poolName, usage.getUsed()));
                postGc.forEach((poolName, usage) -> builder.postGcFreeMem(poolName, usage.getUsed()));

                final GcEventData data = builder.build();

                LOG.debug("Saving garbage collection data.");
                gcEventFactory.save(data);

            // CHECK_OFF: IllegalCatch
            } catch (Exception ex) {
                LOG.error("Caught exception building garbage collection metric.", ex);
            }
            // CHECK_ON: IllegalCatch

        }
    }

    public EventFactory<GcEventData> getGcEventFactory() {
        return gcEventFactory;
    }

}
