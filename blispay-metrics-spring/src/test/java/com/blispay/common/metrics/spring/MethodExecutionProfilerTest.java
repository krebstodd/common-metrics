package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.TransactionDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.model.call.internal.InternalAction;
import com.blispay.common.metrics.model.call.internal.InternalResource;
import com.blispay.common.metrics.spring.annotation.Profiled;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Class MethodExecutionProfilerTest.
 */
public class MethodExecutionProfilerTest {

    private static final String METRIC_NAME = "test-name";
    private static final Duration SIMULATED_LATENCY = Duration.ofSeconds(1);

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

    /**
     * Method testProfileMethodExecution.
     *
     */
    @Test
    public void testProfileMethodExecution() {

        final ConfigurableListableBeanFactory beanFactory = TestSpringConfig.buildContext().getBeanFactory();

        final ProfiledClass profiled = beanFactory.getBean(ProfiledClass.class);
        final MetricService metricService = beanFactory.getBean(MetricService.class);

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        metricService.addEventSubscriber(subscriber);

        profiled.testProfile();

        assertTrue(profiled.wasExecuted());
        assertEquals(1, subscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                                                                        .setApplication(metricService.getApplicationId())
                                                                        .setGroup(EventGroup.INTERNAL_METHOD_CALL)
                                                                        .setName("execute")
                                                                        .setType(EventType.TRANSACTION)
                                                                        .setDataMatcher(new TransactionDataMatcher(InternalResource.fromClass(ProfiledClass.class),
                                                                                                                   InternalAction.fromMethodName("testProfile"),
                                                                                                                   Direction.INTERNAL,
                                                                                                                   Status.success(),
                                                                                                                   1000L))
                                                                        .build();

        assertThat((EventModel<TransactionData, Void>) subscriber.poll(), matcher);
    }

    /**
     * Method testProfileMethodExecutionWithException.
     *
     */
    @Test
    public void testProfileMethodExecutionWithException() {
        final ConfigurableListableBeanFactory beanFactory = TestSpringConfig.buildContext(Boolean.TRUE).getBeanFactory();

        final ProfiledClass profiled = beanFactory.getBean(ProfiledClass.class);
        final MetricService metricService = beanFactory.getBean(MetricService.class);

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        metricService.addEventSubscriber(subscriber);

        // Test throws the exception up the stack
        thrown.expect(RuntimeException.class);
        profiled.testProfile();

        // Test emits event w/ error status.
        assertTrue(profiled.wasExecuted());
        assertEquals(1, subscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                                                                        .setApplication(metricService.getApplicationId())
                                                                        .setGroup(EventGroup.INTERNAL_METHOD_CALL)
                                                                        .setName("execute")
                                                                        .setType(EventType.TRANSACTION)
                                                                        .setDataMatcher(new TransactionDataMatcher(InternalResource.fromClass(ProfiledClass.class),
                                                                                                                   InternalAction.fromMethodName("testProfile"),
                                                                                                                   Direction.INTERNAL,
                                                                                                                   Status.error(),
                                                                                                                   1000L))
                                                                        .build();

        assertThat((EventModel<TransactionData, Void>) subscriber.poll(), matcher);
    }

    /**
     * Class ProfiledClass.
     */
    public static class ProfiledClass {

        private final AtomicBoolean executed = new AtomicBoolean(Boolean.FALSE);

        private final Optional<RuntimeException> ex;

        /**
         * Constructs ProfiledClass.
         */
        public ProfiledClass() {
            this(Optional.empty());
        }

        /**
         * Constructs ProfiledClass.
         *
         * @param exceptionOptional exceptionOptional.
         */
        public ProfiledClass(final Optional<RuntimeException> exceptionOptional) {
            this.ex = exceptionOptional;
        }

        /**
         * Profiled method.
         */
        @Profiled(METRIC_NAME)
        public void testProfile() {
            try {
                Thread.sleep(SIMULATED_LATENCY.toMillis());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

            executed.set(Boolean.TRUE);
            ex.ifPresent(
                exception -> {
                    throw exception;
                });
        }

        /**
         * Method wasExecuted.
         *
         * @return return value.
         */
        public Boolean wasExecuted() {
            return executed.get();
        }

    }

}
