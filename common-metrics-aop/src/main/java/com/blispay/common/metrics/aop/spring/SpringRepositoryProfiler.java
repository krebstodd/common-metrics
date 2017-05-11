package com.blispay.common.metrics.aop.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.aop.aspectj.AbstractFunctionProfiler;
import com.blispay.common.metrics.aop.aspectj.BasicFunctionProfiler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect responsible for profiling public methods from spring repository. Allows developers to time query execution
 * on a per repository method basis.
 */
@Aspect
public class SpringRepositoryProfiler extends AbstractFunctionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(BasicFunctionProfiler.class);

    public SpringRepositoryProfiler(final MetricService metricService) {
        super(metricService);
    }

    @Override
    protected Logger logger() {
        return LOG;
    }

    // CHECK_OFF: IllegalThrows

    /**
     * Aspectj advice surrounding a method call. Starts a transaction metric, calls {@link ProceedingJoinPoint#proceed()}
     * to continue method execution, and then stops the transaction metric.
     *
     * @param joinPoint Join point to profile.
     * @return Results.
     * @throws Throwable An exception was thrown by the join point. The profiler will catch the ex, stop the metric,
     *                   and rethrow the exception.
     */
    @Around("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public Object aroundAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
        return super.timeExecution(joinPoint);
    }

    // CHECK_ON: IllegalThrows

    @Override
    public String getMetricName(final JoinPoint jp) {
        return "spring-repository-query";
    }

}
