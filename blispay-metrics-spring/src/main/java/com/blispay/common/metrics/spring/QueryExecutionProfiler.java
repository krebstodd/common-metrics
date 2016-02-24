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

@Aspect
public class QueryExecutionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutionProfiler.class);

    private final MetricService metricService;

    public QueryExecutionProfiler(final MetricService metricService) {
        this.metricService = metricService;
    }

    @Around("target(com.blispay.common.metrics.spring.ProfiledRepository)")
    public Object around (final ProceedingJoinPoint jPoint) throws Throwable {

        final Class<?> declaringClass = JoinPointUtil.getDeclaringClass(jPoint);
        final String methodName = JoinPointUtil.getMethodName(jPoint);

        LOG.debug("Starting query execution profile for class [{}] with method [{}]", declaringClass, methodName);

        final Optional<ProfiledQuery> optQueryInfo = JoinPointUtil.getAnnotation(jPoint, ProfiledQuery.class);

        if (!optQueryInfo.isPresent()) {
            LOG.error("Unable to locate ProfiledQuery annotation for class [{}] with method [{}]", declaringClass, methodName);
            return jPoint.proceed();
        }

        final ProfiledQuery annotation = optQueryInfo.get();
        final ResourceCallTimer.StopWatch sw = startTimer(annotation.name(), annotation.schema(), annotation.table(), annotation.action());

        try {

            final Object result = jPoint.proceed();
            sw.stop(Status.success());

            LOG.debug("Query execution profile complete for class [{}] with method [{}]", declaringClass, methodName);
            return result;

        } catch (Throwable throwable) {

            sw.stop(Status.error());
            throw throwable;

        }

    }

    private ResourceCallTimer.StopWatch startTimer(final String metricName, final String schema, final String table, final DsAction action) {
        return metricService.createDataSourceCallTimer(MetricGroup.CLIENT_JDBC, metricName)
                .start(DsResource.fromSchemaTable(schema, table), action, null);
    }


}
