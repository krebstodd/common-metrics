package com.blispay.common.metrics;

import com.blispay.common.metrics.model.TrackingInfo;

import java.time.ZonedDateTime;

public class NoOpResourceCounter implements ResourceCounter {

    @Override
    public void updateCount(final Double count) {

    }

    @Override
    public void updateCount(final Double count, final Object userData) {

    }

    @Override
    public void updateCount(final ZonedDateTime timestamp, final Double count, final Object userData, final TrackingInfo trackingInfo) {

    }
}
