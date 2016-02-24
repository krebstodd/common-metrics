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

@Aspect
public class MethodExecutionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodExecutionProfiler.class);

    private static final String defaultName = "internal-call";

    private final MetricService metricService;

    public MethodExecutionProfiler(final MetricService metricService) {
        this.metricService = metricService;
    }

    @Around("execution(* *(..)) && @annotation(com.blispay.common.metrics.spring.annotation.Profiled)")
    public Object around (final ProceedingJoinPoint jPoint) throws Throwable {

        final Class<?> declaringClass = JoinPointUtil.getDeclaringClass(jPoint);
        final String methodName = JoinPointUtil.getMethodName(jPoint);

        LOG.debug("Starting method execution profile for class [{}] with method [{}]", declaringClass, methodName);

        final ResourceCallTimer.StopWatch sw = startTimer(
                JoinPointUtil.getAnnotation(jPoint, Profiled.class).map(Profiled::value).orElse(defaultName),
                declaringClass,
                methodName);

        try {

            final Object result = jPoint.proceed();
            sw.stop(Status.success());

            LOG.debug("Method execution profile complete for class [{}] with method [{}]", declaringClass, methodName);
            return result;

        } catch (Throwable throwable) {

            sw.stop(Status.error());
            throw throwable;

        }

    }

    private ResourceCallTimer.StopWatch startTimer(final String metricName, final Class<?> resource, final String method) {
        return metricService.createInternalResourceCallTimer(MetricGroup.INTERNAL_METHOD_CALL, metricName)
                .start(InternalResource.fromClass(resource), InternalAction.fromMethodName(method));
    }

}
