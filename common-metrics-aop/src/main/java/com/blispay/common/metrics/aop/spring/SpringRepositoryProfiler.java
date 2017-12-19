package com.blispay.common.metrics.aop.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.aop.aspectj.AbstractFunctionProfiler;
import com.blispay.common.metrics.aop.aspectj.AopResource;
import com.blispay.common.metrics.aop.aspectj.BasicFunctionProfiler;
import com.blispay.common.metrics.aop.aspectj.JoinPointUtil;
import com.blispay.common.metrics.model.call.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Aspect responsible for profiling public methods from spring repository. Allows developers to time query execution
 * on a per repository method basis.
 */
@Aspect
public class SpringRepositoryProfiler extends AbstractFunctionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(BasicFunctionProfiler.class);

    /**
     * Create a new spring repository profiler.
     * @param metricService Metric service.
     */
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
    protected String getMetricName(final JoinPoint jp) {
        return "spring-repository-query";
    }

    /**
     * Override the default get resource call and try to find the blispay repository interface. If none is found,
     * just return default.
     *
     * @param jp Join point.
     * @return Resource.
     */
    @Override
    protected Resource getResource(final JoinPoint jp) {
        return findBlispayRepositoryIface(jp)
                .map(iface -> (Resource) AopResource.withName(iface.getName()))
                .orElseGet(() -> super.getResource(jp));
    }

    private static Optional<Class<?>> findBlispayRepositoryIface(final JoinPoint jp) {
        return Arrays.stream(JoinPointUtil.getTargetClass(jp).getInterfaces())
                .filter(iface -> iface.getPackage().getName().startsWith("com.blispay")) // Filter out any ifaces not in a blispay package.
                .filter(iface -> iface.getSimpleName().toLowerCase(Locale.ROOT).contains("repository")) // Filter out any ifaces w/ class name not containing repository.
                .findFirst();
    }


}
