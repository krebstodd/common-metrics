package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AnnotatedFunctionProfilerUnitTest {

    private final MetricService metricService = new MetricService(UUID.randomUUID().toString());
    private final ProfiledProxyFactory proxyFactory = new ProfiledProxyFactory(metricService);

    private TestEventSubscriber metricSubscriber = new TestEventSubscriber();

    @Before
    public void init() {
        metricService.start();
        metricService.addEventSubscriber(metricSubscriber);
    }

    @After
    public void destroy() {
        metricService.stop();
    }

    @Test
    public void testProfilesSuccessfulMethodExecution() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAnnotatedMethods(testObject);

        final String argument = UUID.randomUUID().toString();
        proxied.annotatedWithConfiguration(argument);

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());

        final EventModel published = metricSubscriber.poll();
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals("Expected resource to be supplied by annotation", TestObject.RESOURCE, transactionData.getResource().getValue());
        assertEquals("Expected action to be supplied by annotation", TestObject.ACTION, transactionData.getAction().getValue());
        assertEquals("Expected name to be supplied by annotation", TestObject.NAME, published.getHeader().getName());
        assertEquals("Expected successful status", Status.success().getValue(), transactionData.getStatus());

        assertEquals("Expected method call to be proxied to target", argument, testObject.withConfigArg.get());

        assertGenericMetricHeaders(published, testObject);
    }

    @Test
    public void testUsesDefaultsForMissingAnnotationConfigurationItems() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAnnotatedMethods(testObject);

        final String argument = UUID.randomUUID().toString();
        proxied.annotatedWithoutConfiguration(argument);

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());

        final EventModel published = metricSubscriber.poll();
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals("Expected resource to be generated from class name",
                TestObject.class.getName(), transactionData.getResource().getValue());
        assertEquals("Expected action to be generated from method name", "annotatedWithoutConfiguration", transactionData.getAction().getValue());
        assertEquals("Expected name to be generated from method name", "annotatedWithoutConfiguration", published.getHeader().getName());
        assertEquals("Expected successful status", Status.success().getValue(), transactionData.getStatus());

        assertEquals("Expected method call to be proxied to target", argument, testObject.withoutConfigArg.get());

        assertGenericMetricHeaders(published, testObject);
    }

    @Test
    public void testProfilesNoArgumentMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAnnotatedMethods(testObject);

        proxied.noArguments();

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());

        final EventModel published = metricSubscriber.poll();
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals("Expected resource to be generated from class name",
                TestObject.class.getName(), transactionData.getResource().getValue());
        assertEquals("Expected action to be generated from method name", "noArguments", transactionData.getAction().getValue());
        assertEquals("Expected name to be generated from method name", "noArguments", published.getHeader().getName());
        assertEquals("Expected successful status", Status.success().getValue(), transactionData.getStatus());

        assertTrue("Expected method call to be proxied to target", testObject.noArgumentsCalled.get());

        assertGenericMetricHeaders(published, testObject);
    }

    @Test
    public void testProfilesMultiArgumentMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAnnotatedMethods(testObject);

        final String argument1 = UUID.randomUUID().toString();
        final String argument2 = UUID.randomUUID().toString();
        proxied.multiArgs(argument1, argument2);

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());

        final EventModel published = metricSubscriber.poll();
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals("Expected resource to be generated from class name",
                TestObject.class.getName(), transactionData.getResource().getValue());
        assertEquals("Expected action to be generated from method name", "multiArgs", transactionData.getAction().getValue());
        assertEquals("Expected name to be generated from method name", "multiArgs", published.getHeader().getName());

        assertEquals("Expected method call to be proxied to target", argument1, testObject.multiArgs.get().getLeft());
        assertEquals("Expected method call to be proxied to target", argument2, testObject.multiArgs.get().getRight());

        assertEquals("Expected successful status", Status.success().getValue(), transactionData.getStatus());

        assertGenericMetricHeaders(published, testObject);
    }

    @Test
    public void testProfilesExceptionalMethodException() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAnnotatedMethods(testObject);

        try {
            proxied.exceptionMethod();
            fail("Expected exception to be rethrown.");
        // CHECK_OFF: IllegalCatch
        } catch (RuntimeException ex) {
            assertEquals("Expected ex to be rethrown", testObject.exceptionMessage, ex.getMessage());
        }
        // CHECK_ON: IllegalCatch

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());

        final EventModel published = metricSubscriber.poll();
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals("Expected resource to be generated from class name",
                TestObject.class.getName(), transactionData.getResource().getValue());
        assertEquals("Expected action to be generated from method name", "exceptionMethod", transactionData.getAction().getValue());
        assertEquals("Expected name to be generated from method name", "exceptionMethod", published.getHeader().getName());
        assertEquals("Expected error status", Status.error().getValue(), transactionData.getStatus());

        assertGenericMetricHeaders(published, testObject);
    }

    @Test
    public void testIgnoresNonAnnotatedMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAnnotatedMethods(testObject);
        final String argument = UUID.randomUUID().toString();
        proxied.nonProfiledMethod(argument);
        assertEquals("Expected no transaction metrics to be published", 0, metricSubscriber.count());
        assertEquals("Expected method call to be proxied to target", argument, testObject.nonProfiledArg.get());
    }

    private static void assertGenericMetricHeaders(final EventModel published, final TestObject testObject) {
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals(Direction.INTERNAL, transactionData.getDirection());
        assertEquals(EventGroup.INTERNAL_METHOD_CALL, published.getHeader().getGroup());

        final long durationBuffer = 100;
        assertThat(transactionData.getDurationMillis(), greaterThanOrEqualTo(testObject.sleepMillis - durationBuffer));
        assertThat(transactionData.getDurationMillis(), lessThanOrEqualTo(testObject.sleepMillis + durationBuffer));
    }

    private static class TestObject {

        private static final String RESOURCE = "testClassResource";
        private static final String ACTION = "testAction";
        private static final String NAME = "testName";

        private final String exceptionMessage = UUID.randomUUID().toString();
        private final int sleepMillis = RandomUtils.nextInt(100, 2000);

        private final AtomicReference<String> withConfigArg = new AtomicReference<>();
        private final AtomicReference<String> withoutConfigArg = new AtomicReference<>();
        private final AtomicReference<String> nonProfiledArg = new AtomicReference<>();
        private final AtomicReference<Pair<String, String>> multiArgs = new AtomicReference<>();
        private final AtomicBoolean noArgumentsCalled = new AtomicBoolean();

        @Profiled(
                resource = RESOURCE,
                action = ACTION,
                name = NAME
        )
        public void annotatedWithConfiguration(final String arg) {
            doSleep();
            withConfigArg.set(arg);
        }

        @Profiled
        public void annotatedWithoutConfiguration(final String arg) {
            doSleep();
            withoutConfigArg.set(arg);
        }

        public void nonProfiledMethod(final String arg) {
            doSleep();
            nonProfiledArg.set(arg);
        }

        @Profiled
        public void multiArgs(final String s1, final String s2) {
            doSleep();
            multiArgs.set(Pair.of(s1, s2));
        }

        @Profiled
        public void noArguments() {
            doSleep();
            noArgumentsCalled.set(true);
        }

        @Profiled
        public void exceptionMethod() {
            doSleep();
            throw new RuntimeException(exceptionMessage);
        }

        private void doSleep() {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
