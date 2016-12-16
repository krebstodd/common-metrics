package com.blispay.common.metrics.transaction;

/**
 * Class NoOpTransactionFactory.
 */
public class NoOpTransactionFactory implements TransactionFactory {

    @Override
    public Transaction create() {
        return new NoOpTransaction();
    }

}
