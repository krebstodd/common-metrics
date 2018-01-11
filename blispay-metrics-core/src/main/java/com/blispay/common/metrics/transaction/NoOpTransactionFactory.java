package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;

/**
 * Class NoOpTransactionFactory.
 */
public class NoOpTransactionFactory implements TransactionFactory {

    private static final String NO_OP = "no-op";

    @Override
    public Transaction create() {
        return new TransactionImpl(new NoOpEmitter(), NO_OP, EventGroup.NOOP, NO_OP);
    }

    @Override
    public ManualTransaction createManual() {
        return new ManualTransactionImpl(new NoOpEmitter(), NO_OP, EventGroup.NOOP, NO_OP);
    }

    /**
     * No-op emitter implementation.
     */
    private static final class NoOpEmitter implements EventEmitter {
        @Override
        public void emit(final EventModel event) {
            // noop
        }
    }

}