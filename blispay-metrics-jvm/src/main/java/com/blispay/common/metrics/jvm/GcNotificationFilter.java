package com.blispay.common.metrics.jvm;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * Class GcNotificationFilter.
 */
public class GcNotificationFilter implements NotificationFilter {

    @Override
    public boolean isNotificationEnabled(final Notification notification) {
        return notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION);
    }

}
