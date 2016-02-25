package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.ResourceCallDataMatcher;
import com.blispay.common.metrics.matchers.ResourceCallMetricMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
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

public class MethodExecutionProfilerTest {

    private static final String metricName = "test-name";
    private static final Duration simulatedLatency = Duration.ofSeconds(1);

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

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
        assertThat(subscriber.poll(), new ResourceCallMetricMatcher(MetricGroup.INTERNAL_METHOD_CALL, metricName, MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(InternalResource.fromClass(ProfiledClass.class), InternalAction.fromMethodName("testProfile"), Direction.INTERNAL, Status.success(), 1000L, null)));

    }

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
        assertThat(subscriber.poll(), new ResourceCallMetricMatcher(MetricGroup.INTERNAL_METHOD_CALL, metricName, MetricType.RESOURCE_CALL,
                new ResourceCallDataMatcher(InternalResource.fromClass(ProfiledClass.class), InternalAction.fromMethodName("testProfile"), Direction.INTERNAL, Status.error(), 1000L, null)));
    }

    public static class ProfiledClass {

        private final AtomicBoolean executed = new AtomicBoolean(Boolean.FALSE);

        private final Optional<RuntimeException> ex;

        public ProfiledClass() {
            this(Optional.empty());
        }

        public ProfiledClass(final Optional<RuntimeException> exceptionOptional) {
            this.ex = exceptionOptional;
        }

        /**
         * Profiled method.
         */
        @Profiled(metricName)
        public void testProfile() {
            try {
                Thread.sleep(simulatedLatency.toMillis());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

            executed.set(Boolean.TRUE);
            ex.ifPresent(exception -> {
                    throw exception;
                });
        }

        public Boolean wasExecuted() {
            return executed.get();
        }

    }

}
