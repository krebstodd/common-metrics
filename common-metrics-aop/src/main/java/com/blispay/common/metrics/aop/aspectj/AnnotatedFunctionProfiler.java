package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Resource;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect class used to create proxy objects that time method execution of annotated methods on their targets.
 */
@Aspect
public class AnnotatedFunctionProfiler extends AbstractFunctionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedFunctionProfiler.class);

    /**
     * Create a new annotated function profiler.
     *
     * @param metricService Metric service to publish against.
     */
    public AnnotatedFunctionProfiler(final MetricService metricService) {
        super(metricService);
    }

    // CHECK_OFF: IllegalThrows

    /**
     * Profiles method execution for any public, protected, or package private method annotated with {@link Profiled}.
     * @param joinPoint Join point to target.
     * @return Results of target method.
     * @throws Throwable Thrown by target method during execution.
     */
    @Around("execution (* *(..)) && @annotation(com.blispay.common.metrics.aop.aspectj.Profiled)")
    public Object aroundAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
        return super.timeExecution(joinPoint);
    }

    // CHECK_ON: IllegalThrows

    @Override
    protected Logger logger() {
        return LOG;
    }

    @Override
    protected Action getAction(final JoinPoint joinPoint) {
        final Profiled profiled = getProfiledAnnotation(joinPoint);

        if (StringUtils.isBlank(profiled.action())) {
            return super.getAction(joinPoint);
        } else {
            return AopAction.withName(profiled.action());
        }
    }

    @Override
    protected Resource getResource(final JoinPoint joinPoint) {
        final Profiled profiled = getProfiledAnnotation(joinPoint);

        if (StringUtils.isBlank(profiled.resource())) {
            return super.getResource(joinPoint);
        } else {
            return AopResource.withName(profiled.resource());
        }
    }

    @Override
    protected String getMetricName(final JoinPoint joinPoint) {
        final Profiled profiled = getProfiledAnnotation(joinPoint);

        if (StringUtils.isBlank(profiled.name())) {
            return super.getMetricName(joinPoint);
        } else {
            return profiled.name();
        }
    }

    private static Profiled getProfiledAnnotation(final JoinPoint joinPoint) {
        return JoinPointUtil.getAnnotation(joinPoint, Profiled.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unable to locate Profiled annotation on "
                        + "class [%s] method [%s]", JoinPointUtil.getDeclaringClass(joinPoint), JoinPointUtil.getMethodName(joinPoint))));
    }
}
