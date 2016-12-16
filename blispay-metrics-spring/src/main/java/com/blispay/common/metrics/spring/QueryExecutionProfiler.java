package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.ds.DsResource;
import com.blispay.common.metrics.spring.annotation.ProfiledQuery;
import com.blispay.common.metrics.spring.util.JoinPointUtil;
import com.blispay.common.metrics.transaction.Transaction;
import com.blispay.common.metrics.transaction.TransactionFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

//CHECK_OFF: IllegalCatch
//CHECK_OFF: IllegalThrows

/**
 * Class QueryExecutionProfiler.
 */
@Aspect
public class QueryExecutionProfiler {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutionProfiler.class);

    private final TransactionFactory txFactory;

    /**
     * Create a new jdbc query execution aspect profiler.
     * @param metricService Metric service to report to.
     */
    public QueryExecutionProfiler(final MetricService metricService) {

        this.txFactory = metricService.transactionFactory().inDirection(Direction.OUTBOUND).inGroup(EventGroup.CLIENT_JDBC).build();

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

        final Optional<Transaction> tx = createTransaction(joinPoint);

        tx.ifPresent(Transaction::start);

        try {

            final Object result = joinPoint.proceed();
            tx.ifPresent(Transaction::success);

            return result;

        } catch (Throwable throwable) {

            tx.ifPresent(Transaction::error);
            throw throwable;

        }

    }

    private Optional<Transaction> createTransaction(final ProceedingJoinPoint joinPoint) {

        final Class<?> declaringClass = JoinPointUtil.getDeclaringClass(joinPoint);
        final String methodName = JoinPointUtil.getMethodName(joinPoint);

        final Optional<ProfiledQuery> optQueryInfo = JoinPointUtil.getAnnotation(joinPoint, ProfiledQuery.class);

        if (!optQueryInfo.isPresent()) {
            LOG.error("Unable to locate ProfiledQuery annotation for class [{}] with method [{}]", declaringClass, methodName);
            return Optional.empty();
        }

        final ProfiledQuery annotation = optQueryInfo.get();

        return Optional.of(txFactory.create().withName(annotation.name()).withAction(annotation.action()).onResource(DsResource.fromSchemaTable(annotation.schema(), annotation.table())));

    }

}

// CHECK_ON: IllegalCatch
// CHECK_ON: IllegalThrows

