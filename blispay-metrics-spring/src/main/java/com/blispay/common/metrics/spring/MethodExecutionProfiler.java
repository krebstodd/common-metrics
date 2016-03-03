package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.Transaction;
import com.blispay.common.metrics.TransactionFactory;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.spring.util.JoinPointUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

// CHECK_OFF: IllegalCatch
// CHECK_OFF: IllegalThrows

@Aspect
public class MethodExecutionProfiler {

    private final TransactionFactory txFactory;

    public MethodExecutionProfiler(final MetricService metricService) {

        this.txFactory = metricService.transactionFactory()
                .inDirection(Direction.INTERNAL)
                .inGroup(EventGroup.INTERNAL_METHOD_CALL);

    }

    /**
     * Join point for aspectj integration for method execution metrics. Any methods annotated with the Profiled annotation will
     * automatically have performance metrics published.
     *
     * @param joinPoint Information about the join point.
     * @return The response from the method. Defaults to success for non-exception returns and error for exceptions.
     * @throws Throwable Any exceptions the method execution might throw.
     */
    @Around("execution(* *(..)) && @annotation(com.blispay.common.metrics.spring.annotation.Profiled)")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {

        final Transaction tx = createTransaction(joinPoint);

        tx.start();

        try {

            final Object result = joinPoint.proceed();
            tx.success();

            return result;

        } catch (Throwable throwable) {

            if (tx.isRunning()) {
                tx.error();
            }

            throw throwable;

        }

    }

    private Transaction createTransaction(final ProceedingJoinPoint joinPoint) {

        final Class<?> declaringClass = JoinPointUtil.getDeclaringClass(joinPoint);
        final String methodName = JoinPointUtil.getMethodName(joinPoint);

        return txFactory.withName("execute")
                .withAction(InternalAction.fromMethodName(methodName))
                .onResource(InternalResource.fromClass(declaringClass))
                .create();

    }

}

// CHECK_ON: IllegalCatch
// CHECK_ON: IllegalThrows