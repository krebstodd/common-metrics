package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;

/**
 * Transactions are used to time how long some bounded process takes to complete.
 */
public interface Transaction extends AutoCloseable, TransactionMetadata {

    /**
     * Tell the transaction to start it's timer.
     *
     * @return This transaction.
     */
    Transaction start();

    /**
     * Stop the transaction with a {@link Status#success()} status code.
     *
     * @return Total transaction time.
     */
    Duration success();

    /**
     * Stop the transaction with a {@link Status#error()} status code.
     *
     * @return Total transaction time.
     */
    Duration error();

    /**
     * Stop the transaction with a {@link Status#warning()} status code.
     *
     * @return Total transaction time.
     */
    Duration warn();

    /**
     * Stop the transaction with a {@link Status#warning(Integer)} status code.
     *
     * @param level Custom warning level.
     * @return Total transaction time.
     */
    Duration warn(Integer level);

    /**
     * Stop the transaction with a custom {@link Status}.
     *
     * @param callStatus Custom status.
     * @return Total transaction time.
     */
    Duration stop(Status callStatus);

    /**
     * Indicates whether the transaction has been started.
     *
     * @return return value.
     */
    Boolean isRunning();

    /**
     * Get the current elapsed millis without stopping the transaction.
     *
     * @return return value.
     */
    Long elapsedMillis();

}
