package com.blispay.common.metrics.util;

import com.blispay.common.metrics.model.TrackingInfo;

public final class LocalMetricContext {

    private static final InheritableThreadLocal<LocalMetricContext> CURRENT
            = new InheritableThreadLocal<>();

    private String userTrackingId;
    private String agentTrackingId;
    private String sessionTrackingId;
    private String apiTrackingId;

    private LocalMetricContext() {

    }

    public String getUserTrackingId() {
        return userTrackingId;
    }

    public static void setUserTrackingId(final String userTrackingId) {
        get().userTrackingId = userTrackingId;
    }

    public String getAgentTrackingId() {
        return agentTrackingId;
    }

    public static void setAgentTrackingId(final String agentTrackingId) {
        get().agentTrackingId = agentTrackingId;
    }

    public String getSessionTrackingId() {
        return sessionTrackingId;
    }

    public static void setSessionTrackingId(final String sessionTrackingId) {
        get().sessionTrackingId = sessionTrackingId;
    }

    public String getApiTrackingId() {
        return apiTrackingId;
    }

    public static void setApiTrackingId(final String apiTrackingId) {
        get().apiTrackingId = apiTrackingId;
    }

    public static TrackingInfo getTrackingInfo() {
        final LocalMetricContext lmc = get();

        if (lmc.getApiTrackingId() != null
                || lmc.getAgentTrackingId() != null
                || lmc.getSessionTrackingId() != null
                || lmc.getUserTrackingId() != null) {

            return new TrackingInfo(lmc.getUserTrackingId(), lmc.getAgentTrackingId(),
                    lmc.getSessionTrackingId(), lmc.getApiTrackingId());

        } else {
            return null;
        }

    }

    public static LocalMetricContext get() {
        if (CURRENT.get() == null) {
            synchronized (CURRENT) {
                if (CURRENT.get() == null) {
                    CURRENT.set(new LocalMetricContext());
                }
            }
        }

        return CURRENT.get();

    }

    public static void setTrackingInfo(final String userTrackingId, final String agentTrackingId,
                                       final String sessionTrackingId, final String apiTrackingId) {


        final LocalMetricContext ctx = get();

        ctx.setUserTrackingId(userTrackingId);
        ctx.setAgentTrackingId(agentTrackingId);
        ctx.setSessionTrackingId(sessionTrackingId);
        ctx.setApiTrackingId(apiTrackingId);

    }

}
