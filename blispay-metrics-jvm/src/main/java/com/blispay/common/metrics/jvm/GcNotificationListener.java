package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.metric.EventRepository;
import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.MemoryUsage;
import java.util.Map;

public class GcNotificationListener implements NotificationListener {

    private static final String NEW_GEN = "PS Eden Space";
    private static final String SURVIVOR = "PS Survivor Space";
    private static final String OLD_GEN = "PS Old Gen";

    private final EventRepository<GcEventData> gcEventRepo;

    public GcNotificationListener(final EventRepository<GcEventData> gcEventRepo) {
        this.gcEventRepo = gcEventRepo;
    }

    @Override
    public void handleNotification(final Notification notification, final Object handback) {
        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {

            final CompositeData cData = (CompositeData) notification.getUserData();
            final GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo.from(cData);

            final Map<String, MemoryUsage> preGc = gcInfo.getGcInfo().getMemoryUsageBeforeGc();
            final Map<String, MemoryUsage> postGc = gcInfo.getGcInfo().getMemoryUsageAfterGc();

            final GcEventData data = new GcEventData.Builder()
                    .action(gcInfo.getGcAction())
                    .cause(gcInfo.getGcCause())
                    .name(gcInfo.getGcName())
                    .durationMillis(gcInfo.getGcInfo().getDuration())
                    .startTime(gcInfo.getGcInfo().getStartTime())
                    .endTime(gcInfo.getGcInfo().getEndTime())
                    .newGen(preGc.get(NEW_GEN).getUsed(), postGc.get(NEW_GEN).getUsed())
                    .survivor(preGc.get(SURVIVOR).getUsed(), postGc.get(SURVIVOR).getUsed())
                    .oldGen(preGc.get(OLD_GEN).getUsed(), postGc.get(OLD_GEN).getUsed())
                    .build();

            gcEventRepo.save(data);
        }
    }

    public EventRepository<GcEventData> getGcEventRepo() {
        return gcEventRepo;
    }

}
