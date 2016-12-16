package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;

/**
 * Interface Transaction.
 */
public interface Transaction extends AutoCloseable {

    /**
     * Method withName.
     *
     * @param name name.
     * @return return value.
     */
    Transaction withName(String name);

    /**
     * Method withNameFromType.
     *
     * @param type type.
     * @return return value.
     */
    Transaction withNameFromType(Class<?> type);

    /**
     * Method inDirection.
     *
     * @param direction direction.
     * @return return value.
     */
    Transaction inDirection(Direction direction);

    /**
     * Method withAction.
     *
     * @param action action.
     * @return return value.
     */
    Transaction withAction(Action action);

    /**
     * Method onResource.
     *
     * @param resource resource.
     * @return return value.
     */
    Transaction onResource(Resource resource);

    /**
     * Method userData.
     *
     * @param userData userData.
     * @return return value.
     */
    Transaction userData(Object userData);

    /**
     * Method start.
     *
     * @return return value.
     */
    Transaction start();

    /**
     * Method success.
     *
     * @return return value.
     */
    Duration success();

    /**
     * Method error.
     *
     * @return return value.
     */
    Duration error();

    /**
     * Method warn.
     *
     * @return return value.
     */
    Duration warn();

    /**
     * Method warn.
     *
     * @param level level.
     * @return return value.
     */
    Duration warn(Integer level);

    /**
     * Method stop.
     *
     * @param callStatus callStatus.
     * @return return value.
     */
    Duration stop(Status callStatus);

    /**
     * Method isRunning.
     *
     * @return return value.
     */
    Boolean isRunning();

    /**
     * Method elapsedMillis.
     *
     * @return return value.
     */
    Long elapsedMillis();

}
