package com.blispay.common.metrics;

import com.codahale.metrics.Gauge;

public interface BpMetricListener {

    public void onGaugeAdded(final String s, final Gauge<?> gauge);

    public void onGaugeRemoved(final String s);

    public void onCounterAdded(final BpCounter counter);

    public void onCounterRemoved(final String s);

    public void onHistogramAdded(final BpHistogram histogram);

    public void onHistogramRemoved(final String s);

    public void onMeterAdded(final BpMeter meter);

    public void onMeterRemoved(final String s);

    public void onTimerAdded(final BpTimer timer);

    public void onTimerRemoved(final String s);

    public void stop();

    public void start();

}
