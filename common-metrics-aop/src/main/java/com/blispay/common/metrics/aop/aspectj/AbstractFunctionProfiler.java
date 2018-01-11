package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.transaction.Transaction;
import com.blispay.common.metrics.transaction.TransactionFactory;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

/**
 * Base aspect class used to create proxy objects that time method execution. It is the responsibility of the
 * implementing class to declare pointcut methods and annotate themselves with {@link org.aspectj.lang.annotation.Aspect}
 * annotations.
 */
public abstract class AbstractFunctionProfiler {

    private final MetricService metricService;

    protected AbstractFunctionProfiler(final MetricService metricService) {
        Validate.notNull(metricService, "Metric service required.");
        this.metricService = metricService;
    }

    // CHECK_OFF: IllegalThrows

    protected Object timeExecution(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Transaction tx = safeStart(createTransactionFactory(metricService, joinPoint).create());
        boolean joinPointExecuted = false;
        try {
            final Object results = joinPoint.proceed();
            joinPointExecuted = true;
            return results;
        } finally {
            if (joinPointExecuted) {
                safeStop(tx, Status.success());
            } else {
                safeStop(tx, Status.error());
            }
        }
    }

    // CHECK_ON: IllegalThrows

    protected abstract Logger logger();

    protected Action getAction(final JoinPoint joinPoint) {
        return AopAction.withName(JoinPointUtil.getMethodName(joinPoint));
    }

    protected Resource getResource(final JoinPoint joinPoint) {
        return AopResource.withName(JoinPointUtil.getTargetClass(joinPoint).getName());
    }

    protected String getMetricName(final JoinPoint joinPoint) {
        return JoinPointUtil.getMethodName(joinPoint);
    }

    private TransactionFactory createTransactionFactory(final MetricService metricService,
                                                        final JoinPoint joinPoint) {

        return metricService.transactionFactory()
                .inDirection(Direction.INTERNAL)
                .inGroup(EventGroup.INTERNAL_METHOD_CALL)
                .withAction(getAction(joinPoint))
                .onResource(getResource(joinPoint))
                .withName(getMetricName(joinPoint))
                .build();
    }

    private Transaction safeStart(final Transaction transaction) {
        try {
            return transaction.start();
        // CHECK_OFF: IllegalCatch
        } catch (final Exception ex) {
            logger().error("Caught exception attempting to start function profile transaction", ex);
            return null;
        }
        // CHECK_ON: IllegalCatch
    }

    private void safeStop(final Transaction transaction, final Status status) {
        try {
            if (transaction != null) {
                transaction.stop(status);
            }
        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {
            logger().error("Caught exception attempting to stop function profile transaction", ex);
        }
        // CHECK_ON: IllegalCatch
    }

}
