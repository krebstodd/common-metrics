package com.blispay.common.metrics.transaction;

import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;

/**
 * Class TransactionFactoryImpl.
 */
public class TransactionFactoryImpl implements TransactionFactory {

    private final String applicationId;
    private final EventEmitter emitter;

    private final EventGroup group;
    private final String name;
    private final Direction direction;
    private final Action action;
    private final Resource resource;

    /**
     * Constructs TransactionFactoryImpl.
     *
     * @param applicationId applicationId.
     * @param emitter emitter.
     * @param group group.
     * @param name name.
     * @param direction direction.
     * @param action action.
     * @param resource resource.
     */
    protected TransactionFactoryImpl(final String applicationId,
                                     final EventEmitter emitter,
                                     final EventGroup group,
                                     final String name,
                                     final Direction direction,
                                     final Action action,
                                     final Resource resource) {

        this.applicationId = applicationId;
        this.emitter = emitter;
        this.group = group;
        this.name = name;
        this.direction = direction;
        this.action = action;
        this.resource = resource;

    }

    /**
     * Create a new transaction with the currently configured state.
     *
     * @return Transaction instance.
     */
    @Override
    public Transaction create() {
        return fillMetadata(new TransactionImpl(emitter, applicationId, group, name));
    }

    @Override
    public ManualTransaction createManual() {
        return fillMetadata(new ManualTransactionImpl(emitter, applicationId, group, name));
    }

    private <T extends AbstractTransaction> T fillMetadata(final T metadata) {
        return metadata.inDirection(this.direction)
                .withAction(this.action)
                .onResource(this.resource);
    }

}
