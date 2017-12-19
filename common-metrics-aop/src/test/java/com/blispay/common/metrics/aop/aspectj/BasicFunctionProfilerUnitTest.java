package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BasicFunctionProfilerUnitTest {

    private final MetricService metricService = new MetricService(UUID.randomUUID().toString());
    private final ProfiledProxyFactory proxyFactory = new ProfiledProxyFactory(metricService);

    private TestEventSubscriber metricSubscriber = new TestEventSubscriber();

    /**
     * Init.
     */
    @Before
    public void init() {
        metricService.start();
        metricService.addEventSubscriber(metricSubscriber);
    }

    /**
     * Destroy.
     */
    @After
    public void destroy() {
        metricService.stop();
    }

    @Test
    public void testProfilesPublicMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAllMethods(testObject);

        proxied.publicMethod();

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());

        final EventModel published = metricSubscriber.poll();
        assertMetricFieldsSet(published, "publicMethod");

        assertTrue(testObject.publicExecuted);
        assertFalse(testObject.privateExectuted);
        assertFalse(testObject.packagePrivateExecuted);
        assertFalse(testObject.protectedExecuted);
    }

    @Test
    public void testProfilesPackagePrivateMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAllMethods(testObject);

        proxied.packagePrivateMethod();

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());
        final EventModel published = metricSubscriber.poll();
        assertMetricFieldsSet(published, "packagePrivateMethod");

        assertFalse(testObject.publicExecuted);
        assertFalse(testObject.privateExectuted);
        assertTrue(testObject.packagePrivateExecuted);
        assertFalse(testObject.protectedExecuted);
    }

    @Test
    public void testDoesNotProfilePrivateMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAllMethods(testObject);
        proxied.privateMethod();
        assertEquals("Expected no transaction metric", 0, metricSubscriber.count());
    }

    @Test
    public void testProfilesProtectedMethods() {
        final TestObject testObject = new TestObject();
        final TestObject proxied = proxyFactory.profileAllMethods(testObject);

        proxied.protectedMethod();

        assertEquals("Expected one transaction metric", 1, metricSubscriber.count());
        final EventModel published = metricSubscriber.poll();
        assertMetricFieldsSet(published, "protectedMethod");

        assertFalse(testObject.publicExecuted);
        assertFalse(testObject.privateExectuted);
        assertFalse(testObject.packagePrivateExecuted);
        assertTrue(testObject.protectedExecuted);
    }

    private static void assertMetricFieldsSet(final EventModel published, final String methodName) {
        final TransactionData transactionData = (TransactionData) published.getData();
        assertEquals(Direction.INTERNAL, transactionData.getDirection());
        assertEquals(EventGroup.INTERNAL_METHOD_CALL, published.getHeader().getGroup());
        assertThat(transactionData.getDurationMillis(), greaterThanOrEqualTo(0L));
        assertEquals("Expected resource to equal class name", TestObject.class.getName(), transactionData.getResource().getValue());
        assertEquals("Expected action to equal method name", methodName, transactionData.getAction().getValue());
        assertEquals("Expected name to equal method name", methodName, published.getHeader().getName());
        assertEquals("Expected successful status", Status.success().getValue(), transactionData.getStatus());
    }

    /**
     * Test object.
     */
    public static class TestObject {

        private boolean publicExecuted;
        private boolean privateExectuted;
        private boolean protectedExecuted;
        private boolean packagePrivateExecuted;

        void publicMethod() {
            publicExecuted = true;
        }

        private void privateMethod() {
            privateExectuted = true;
        }

        protected void protectedMethod() {
            protectedExecuted = true;
        }

        void packagePrivateMethod() {
            packagePrivateExecuted = true;
        }

    }
}
