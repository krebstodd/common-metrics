package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.metric.ResourceCallTimer;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.ds.DsAction;
import com.blispay.common.metrics.model.call.ds.DsResource;
import com.blispay.common.metrics.spring.annotation.ProfiledQuery;
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
public class QueryExecutionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutionProfiler.class);

    private final MetricService metricService;

    public QueryExecutionProfiler(final MetricService metricService) {
        this.metricService = metricService;
    }

    /**
     * Join point for aspectj integration for query execution metrics. Any class that implements the profiled repository class
     * will automatically be scanned. If any methods on the class contain a profiled query annotation, that query will
     * automatically have performance metrics published.
     *
     * @param joinPoint Information about the join point.
     * @return The response from the query.
     * @throws Throwable Any exceptions the method execution might throw.
     */
    @Around("target(com.blispay.common.metrics.spring.ProfiledRepository)")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {

        final Optional<ResourceCallTimer.StopWatch> sw = safeStart(joinPoint);
        final Object result;

        try {

            result = joinPoint.proceed();

        } catch (Throwable throwable) {

            if (sw != null) {
                safeStop(sw, Status.error());
            }

            throw throwable;
        }

        safeStop(sw, Status.success());

        return result;

    }

    private Optional<ResourceCallTimer.StopWatch> safeStart(final ProceedingJoinPoint joinPoint) {

        try {

            LOG.debug("Attempting to safe-start query timer.");

            final Class<?> declaringClass = JoinPointUtil.getDeclaringClass(joinPoint);
            final String methodName = JoinPointUtil.getMethodName(joinPoint);

            LOG.debug("Starting query execution profile for class [{}] with method [{}]", declaringClass, methodName);

            final Optional<ProfiledQuery> optQueryInfo = JoinPointUtil.getAnnotation(joinPoint, ProfiledQuery.class);

            if (!optQueryInfo.isPresent()) {
                LOG.error("Unable to locate ProfiledQuery annotation for class [{}] with method [{}]", declaringClass, methodName);
                return Optional.empty();
            }

            final ProfiledQuery annotation = optQueryInfo.get();
            return Optional.of(startTimer(annotation.name(), annotation.schema(), annotation.table(), annotation.action()));

        } catch (Throwable throwable) {
            LOG.error("Caught throwable attempting to start timer for profiled query.", throwable);
            return Optional.empty();
        }

    }

    private void safeStop(final Optional<ResourceCallTimer.StopWatch> sw, final Status status) {
        try {

            sw.ifPresent(watch -> watch.stop(status));

        } catch (Throwable throwable) {
            LOG.error("Caught throwable attempting stop timer for profiled query", throwable);
        }
    }

    private ResourceCallTimer.StopWatch startTimer(final String metricName, final String schema, final String table, final DsAction action) {
        return metricService.createDataSourceCallTimer(MetricGroup.CLIENT_JDBC, metricName)
                .start(DsResource.fromSchemaTable(schema, table), action, null);
    }


}

// CHECK_ON: IllegalCatch
// CHECK_ON: IllegalThrows