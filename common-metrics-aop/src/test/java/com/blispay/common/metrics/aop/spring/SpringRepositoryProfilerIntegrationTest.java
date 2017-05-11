package com.blispay.common.metrics.aop.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.aop.aspectj.AopAction;
import com.blispay.common.metrics.aop.aspectj.AopResource;
import com.blispay.common.metrics.aop.testutils.SpringRepositoryTestConfig;
import com.blispay.common.metrics.aop.testutils.TestRepository;
import com.blispay.common.metrics.aop.testutils.TestRepositoryImpl;
import com.blispay.common.metrics.aop.testutils.TestService;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.TransactionDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SpringRepositoryProfilerIntegrationTest {

    private AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
    private TestEventSubscriber eventSubscriber = new TestEventSubscriber();
    private MetricService metricService;
    private TestRepository testRepository;
    private TestService testService;

    /**
     * Init spring context.
     */
    @Before
    public void init() {
        springContext.register(SpringRepositoryTestConfig.class);
        springContext.refresh();
        springContext.start();

        this.testRepository = springContext.getBean(TestRepository.class);
        this.testService = springContext.getBean(TestService.class);

        this.metricService = springContext.getBean(MetricService.class);
        this.metricService.addEventSubscriber(eventSubscriber);
    }

    /**
     * Destroy context.
     */
    @After
    public void destroy() {
        this.springContext.stop();
        this.springContext.destroy();
    }

    @Test
    public void testProfilesReadMethods() {
        assertEquals(0, this.eventSubscriber.count());
        testRepository.findOne(UUID.randomUUID().toString());
        assertEquals("Expected one metric to be published", 1, this.eventSubscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.INTERNAL_METHOD_CALL)
                .setName("spring-repository-query")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(
                        AopResource.withName(TestRepository.class.getName()),
                        AopAction.withName("findOne"),
                        Direction.INTERNAL,
                        Status.success(),
                        getApproximateLatency(),
                        100L))
                .build();

        assertThat(eventSubscriber.peek(), hasItem(matcher));
    }

    @Test
    public void testProfilesWriteMethods() {
        assertEquals(0, this.eventSubscriber.count());
        testRepository.save(new Object());
        assertEquals("Expected one metric to be published", 1, this.eventSubscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.INTERNAL_METHOD_CALL)
                .setName("spring-repository-query")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(
                        AopResource.withName(TestRepository.class.getName()),
                        AopAction.withName("save"),
                        Direction.INTERNAL,
                        Status.success(),
                        getApproximateLatency(),
                        100L))
                .build();

        assertThat(eventSubscriber.peek(), hasItem(matcher));
    }

    @Test
    public void testProfilesCustomMethods() {
        assertEquals(0, this.eventSubscriber.count());
        testRepository.myCustomMethod("somestring");
        assertEquals("Expected one metric to be published", 1, this.eventSubscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.INTERNAL_METHOD_CALL)
                .setName("spring-repository-query")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(
                        AopResource.withName(TestRepository.class.getName()),
                        AopAction.withName("myCustomMethod"),
                        Direction.INTERNAL,
                        Status.success(),
                        getApproximateLatency(),
                        100L))
                .build();

        assertThat(eventSubscriber.peek(), hasItem(matcher));
    }

    @Test
    public void testProfilesMethodsWithoutArguments() {
        assertEquals(0, this.eventSubscriber.count());
        testRepository.findAll();
        assertEquals("Expected one metric to be published", 1, this.eventSubscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                .setApplication(metricService.getApplicationId())
                .setGroup(EventGroup.INTERNAL_METHOD_CALL)
                .setName("spring-repository-query")
                .setType(EventType.TRANSACTION)
                .setDataMatcher(new TransactionDataMatcher(
                        AopResource.withName(TestRepository.class.getName()),
                        AopAction.withName("findAll"),
                        Direction.INTERNAL,
                        Status.success(),
                        getApproximateLatency(),
                        100L))
                .build();

        assertThat(eventSubscriber.peek(), hasItem(matcher));
    }

    @Test
    public void testIgnoresNonSpringRepositories() {
        testService.noop();
        assertEquals("Only spring repositories should be profiled.", 0, this.eventSubscriber.count());
    }

    private long getApproximateLatency() {
        return TestRepositoryImpl.LATENCY.toMillis();
    }
}
