package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.model.call.Status;

import java.time.Duration;

/**
 * Manual transactions require that some external mechanism time an operation and provide the total execution time
 * to the transaction. See {@link Transaction} for automatically managed execution times.
 */
public interface ManualTransaction extends TransactionMetadata {

    /**
     * Stop the transaction with a {@link Status#success()} status code.
     *
     * @param duration Total transaction time.
     * @return Total transaction time.
     */
    Duration success(Duration duration);

    /**
     * Stop the transaction with a {@link Status#error()} status code.
     *
     * @param duration Total transaction time.
     * @return Total transaction time.
     */
    Duration error(Duration duration);

    /**
     * Stop the transaction with a {@link Status#warning()} status code.
     *
     * @param duration Total transaction time.
     * @return Total transaction time.
     */
    Duration warn(Duration duration);

    /**
     * Stop the transaction with a {@link Status#warning(Integer)} status code.
     *
     * @param level Custom level.
     * @param duration Total transaction time.
     * @return Total transaction time.
     */
    Duration warn(Integer level, Duration duration);

    /**
     * Stop the transaction with a custom {@link Status}.
     *
     * @param callStatus Custom status.
     * @param duration Total transaction time.
     * @return Total transaction time.
     */
    Duration stop(Status callStatus, Duration duration);

}
