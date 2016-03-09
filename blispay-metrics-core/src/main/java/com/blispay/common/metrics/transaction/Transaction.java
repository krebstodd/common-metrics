package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;

public interface Transaction extends AutoCloseable {

    Transaction withName(final String name);

    Transaction withNameFromType(final Class<?> type);

    Transaction inDirection(final Direction direction);

    Transaction withAction(final Action action);

    Transaction onResource(final Resource resource);

    Transaction userData(final Object userData);

    Transaction start();

    Duration success();

    Duration error();

    Duration warn();

    Duration warn(final Integer level);

    Duration stop(final Status callStatus);

    Boolean isRunning();

    Long elapsedMillis();

}