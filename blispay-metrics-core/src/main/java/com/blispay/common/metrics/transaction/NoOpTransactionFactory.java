package com.blispay.common.metrics.transaction;

public class NoOpTransactionFactory implements TransactionFactory {

    @Override
    public Transaction create() {
        return new NoOpTransaction();
    }

}
