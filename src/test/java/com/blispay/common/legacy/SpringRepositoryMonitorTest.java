package com.blispay.common.legacy;

import com.blispay.common.legacy.AbstractMetricsTest;
import com.blispay.common.metrics.legacy.SpringRepositoryMonitor;
import com.codahale.metrics.Timer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class SpringRepositoryMonitorTest extends AbstractMetricsTest {

    private static final String expectedMetricName = "db-query.org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.increment.response-time";

    //CHECK_OFF: MagicNumber
    @Test
    public void testWrapsAndTimesMethodExecution() throws NoSuchFieldException, IllegalAccessException {
        final TestObject obj = new TestObject();
        final ProxyFactory proxyFactory = new ProxyFactory(obj);
        proxyFactory.addAdvice(new SpringRepositoryMonitor.SpringRepositoryProfiler());

        final TestObject proxyObj = (TestObject) proxyFactory.getProxy();

        Assert.assertEquals("The function returns the calculated value", 0, proxyObj.increment());
        Assert.assertEquals("The function executes fully", 1, TestObject.counter.get());

        final Timer executionTimer = getRegistry().getTimers().get(expectedMetricName);
        Assert.assertEquals("The count is incremented for a call", 1, executionTimer.getCount());
        Assert.assertTrue("The execution time is calculated", inRange(executionTimer.getSnapshot().getMean() / 10000000, new Double(1.0), new Double(10.0)));

        proxyObj.increment();
        Assert.assertEquals("The count is incremented for subsequent calls", 2, executionTimer.getCount());
    }
    //CHECK_ON: MagicNumber

    static class TestObject {

        public static final AtomicInteger counter = new AtomicInteger(0);

        public int increment() {
            return counter.getAndIncrement();
        }

    }
}
