package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.metric.ResourceCallTimer;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.spring.annotation.Profiled;
import com.blispay.common.metrics.spring.util.JoinPointUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

// CHECK_OFF: IllegalCatch
// CHECK_OFF: IllegalThrows

@Aspect
public class MethodExecutionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodExecutionProfiler.class);

    private static final String defaultName = "internal-call";

    private final MetricService metricService;

    public MethodExecutionProfiler(final MetricService metricService) {
        this.metricService = metricService;
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

        final Optional<ResourceCallTimer.StopWatch> sw = safeStart(joinPoint);

        final Object result;

        try {

            result = joinPoint.proceed();

        } catch (Throwable throwable) {

            safeStop(sw, Status.error());
            throw throwable;

        }

        safeStop(sw, Status.success());
        return result;

    }

    private Optional<ResourceCallTimer.StopWatch> safeStart(final ProceedingJoinPoint joinPoint) {

        try {

            LOG.debug("Attempting to safe-start stopwatch for method execution");

            final Class<?> declaringClass = JoinPointUtil.getDeclaringClass(joinPoint);
            final String methodName = JoinPointUtil.getMethodName(joinPoint);

            final ResourceCallTimer.StopWatch sw = startTimer(
                    JoinPointUtil.getAnnotation(joinPoint, Profiled.class).map(Profiled::value).orElse(defaultName),
                    declaringClass,
                    methodName);

            LOG.debug("Started timer for method execution for class [{}] with method [{}].", declaringClass, methodName);
            return Optional.of(sw);

        } catch (Throwable throwable) {
            LOG.error("Caught throwable attempting to start timer for profiled method.", throwable);
            return Optional.empty();
        }

    }

    private void safeStop(final Optional<ResourceCallTimer.StopWatch> sw, final Status status) {

        try {

            LOG.debug("Method execution profile complete, safely stopping timer.");
            sw.ifPresent(watch -> watch.stop(status));

        } catch (Throwable throwable) {
            LOG.error("Caught throwable attempting to stop timer for profiled method.", throwable);
        }

    }

    private ResourceCallTimer.StopWatch startTimer(final String metricName, final Class<?> resource, final String method) {
        return metricService.createInternalResourceCallTimer(MetricGroup.INTERNAL_METHOD_CALL, metricName)
                .start(InternalResource.fromClass(resource), InternalAction.fromMethodName(method));
    }

}

// CHECK_ON: IllegalCatch
// CHECK_ON: IllegalThrows