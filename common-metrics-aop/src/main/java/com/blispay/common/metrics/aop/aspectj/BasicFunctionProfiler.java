package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.MetricService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect class used to create proxy objects that time method execution on their targets.
 */
@Aspect
public class BasicFunctionProfiler extends AbstractFunctionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(BasicFunctionProfiler.class);

    public BasicFunctionProfiler(final MetricService metricService) {
        super(metricService);
    }

    @Override
    protected Logger logger() {
        return LOG;
    }

    // CHECK_OFF: IllegalThrows

    /**
     * Profiles method execution for public, protected, and package private methods.
     * @param joinPoint Join point to target.
     * @return Results of target method.
     * @throws Throwable Thrown by target during execution.
     */
    @Around("execution(* *(..))")
    public Object aroundAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
        return super.timeExecution(joinPoint);
    }

    // CHECK_ON: IllegalThrows

}
