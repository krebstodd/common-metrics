package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;

/**
 * Top level metadata included in all {@link AbstractTransaction} instances.
 */
public interface TransactionMetadata {

    /**
     * Method withName.
     *
     * @param name name.
     * @param <T> Impl type.
     * @return return value.
     */
    <T extends AbstractTransaction> T withName(String name);

    /**
     * Method withNameFromType.
     *
     * @param type type.
     * @param <T> Impl type.
     * @return return value.
     */
    <T extends AbstractTransaction> T withNameFromType(Class<?> type);

    /**
     * Method inDirection.
     *
     * @param direction direction.
     * @param <T> Impl type.
     * @return return value.
     */
    <T extends AbstractTransaction> T inDirection(Direction direction);

    /**
     * Method withAction.
     *
     * @param action action.
     * @param <T> Impl type.
     * @return return value.
     */
    <T extends AbstractTransaction> T withAction(Action action);

    /**
     * Method onResource.
     *
     * @param resource resource.
     * @param <T> Impl type.
     * @return return value.
     */
    <T extends AbstractTransaction> T onResource(Resource resource);

    /**
     * Method userData.
     *
     * @param userData userData.
     * @param <T> Impl type.
     * @return return value.
     */
    <T extends AbstractTransaction> T userData(Object userData);

}
