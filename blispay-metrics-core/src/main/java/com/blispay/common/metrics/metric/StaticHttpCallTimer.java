package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.http.HttpAction;
import com.blispay.common.metrics.model.call.http.HttpResource;

public class StaticHttpCallTimer {

    private final HttpCallTimer root;

    private final Direction direction;
    private final HttpResource resource;
    private final HttpAction action;

    public StaticHttpCallTimer(final HttpCallTimer root, final Direction direction, final HttpResource resource, final HttpAction action) {
        this.root = root;

        this.direction = direction;
        this.resource = resource;
        this.action = action;
    }

    public ResourceCallTimer.StopWatch start() {
        return root.start(direction, resource, action);
    }

}
