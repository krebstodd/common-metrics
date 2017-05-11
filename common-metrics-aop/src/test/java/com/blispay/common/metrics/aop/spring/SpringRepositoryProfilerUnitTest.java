package com.blispay.common.metrics.aop.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.call.Action;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Resource;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.transaction.Transaction;
import com.blispay.common.metrics.transaction.TransactionFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECK_OFF: IllegalThrows
// CHECK_OFF: IllegalCatch
public class SpringRepositoryProfilerUnitTest {

    private final MetricService metricService = mock(MetricService.class);
    private final TransactionFactory mockTxFactory = mock(TransactionFactory.class);
    private final TransactionFactory.Builder mockTxFactoryBuilder = mock(TransactionFactory.Builder.class);
    private final Transaction mockTx = mock(Transaction.class);

    private final SpringRepositoryProfiler profiler = new SpringRepositoryProfiler(metricService);

    /**
     * Initialize mocks.
     */
    @Before
    public void initMocks() {
        when(metricService.transactionFactory()).thenReturn(mockTxFactoryBuilder);

        when(mockTxFactoryBuilder.inDirection(any())).thenReturn(mockTxFactoryBuilder);
        when(mockTxFactoryBuilder.inGroup(any())).thenReturn(mockTxFactoryBuilder);
        when(mockTxFactoryBuilder.withAction(any())).thenReturn(mockTxFactoryBuilder);
        when(mockTxFactoryBuilder.onResource(any())).thenReturn(mockTxFactoryBuilder);
        when(mockTxFactoryBuilder.withName(any())).thenReturn(mockTxFactoryBuilder);
        when(mockTxFactoryBuilder.build()).thenReturn(mockTxFactory);

        when(mockTxFactory.create()).thenReturn(mockTx);

        when(mockTx.start()).thenReturn(mockTx);
    }

    @Test
    public void testTimesSuccessfulExecution() throws Throwable {
        final Object results = new Object();

        final ProceedingJoinPoint jp = mockJoinPoint();

        when(jp.proceed()).thenAnswer(invocationOnMock -> {
            assertRunning(mockTx);
            return results;
        });

        final Object actualResults = profiler.aroundAdvice(jp);

        assertEquals(results, actualResults);
        verify(mockTx, times(1)).start();

        final ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(mockTx, times(1)).stop(statusCaptor.capture());
        assertEquals(Status.success().getValue(), statusCaptor.getValue().getValue());
    }

    @Test
    public void testTimesErrorExecution() throws Throwable {
        final RuntimeException ex = new RuntimeException("Some ex.");

        final ProceedingJoinPoint jp = mockJoinPoint();

        when(jp.proceed()).thenAnswer(invocationOnMock -> {
            assertRunning(mockTx);
            throw ex;
        });

        try {
            profiler.aroundAdvice(jp);
            fail("Expected exception to re thrown.");
        } catch (RuntimeException thrown) {
            assertEquals(ex, thrown);
            verify(mockTx, times(1)).start();

            final ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
            verify(mockTx, times(1)).stop(statusCaptor.capture());
            assertEquals(Status.error().getValue(), statusCaptor.getValue().getValue());
        }
    }

    @Test
    public void testBuildsTransactionFactoryFromJoinPoint() throws Throwable {
        final ProceedingJoinPoint jp = mockJoinPoint();
        when(jp.proceed()).thenReturn(new Object());

        profiler.aroundAdvice(jp);

        verify(mockTxFactoryBuilder, times(1)).inDirection(eq(Direction.INTERNAL));
        verify(mockTxFactoryBuilder, times(1)).inGroup(eq(EventGroup.INTERNAL_METHOD_CALL));
        verify(mockTxFactoryBuilder, times(1)).withName("spring-repository-query");

        final ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        verify(mockTxFactoryBuilder, times(1)).withAction(actionCaptor.capture());
        assertEquals("Expected action to be built from mock join point", "initMocks", actionCaptor.getValue().getValue());

        final ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        verify(mockTxFactoryBuilder, times(1)).onResource(resourceCaptor.capture());
        assertEquals("Expected resource to be built from mock join point",
                SpringRepositoryProfilerUnitTest.class.getName(),
                resourceCaptor.getValue().getValue());

    }

    private static ProceedingJoinPoint mockJoinPoint() {
        final ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);

        final MethodSignature mockSig = mock(MethodSignature.class);
        try {
            when(mockSig.getMethod()).thenReturn(SpringRepositoryProfilerUnitTest.class.getMethod("initMocks"));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        when(jp.getTarget()).thenReturn(new SpringRepositoryProfilerUnitTest());
        when(jp.getSignature()).thenReturn(mockSig);

        return jp;
    }

    private void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertRunning(final Transaction tx) {
        verify(tx, times(1)).start();
        verify(tx, never()).stop(any());
        verify(tx, never()).error();
        verify(tx, never()).success();
    }

}
// CHECK_ON: IllegalThrows
// CHECK_ON: IllegalCatch