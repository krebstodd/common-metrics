package com.blispay.common;

import com.blispay.common.metrics.BpCounter;
import com.blispay.common.metrics.BpMetricService;
import org.junit.Test;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class JmxReporterTest {

    private MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

    @Test
    public void testJmxReporter() throws MalformedObjectNameException, InstanceNotFoundException, MBeanException, AttributeNotFoundException, ReflectionException, IOException {
        final BpMetricService service = BpMetricService.getInstance();
        final BpCounter counter = service.createCounter(JmxReporterTest.class, "testJmxReporter", "Test to ensure metrics are accessible through jmx server.");

        final String expectedMetricName = JmxReporterTest.class.getName() + ".testJmxReporter";

        final String result = getCurrentState(expectedMetricName);
        assertEquals("0", getFieldFromOutput(result, "count").get());
        assertEquals("com.blispay.common.JmxReporterTest.testJmxReporter", getFieldFromOutput(result, "name").get());
        assertEquals("Test to ensure metrics are accessible through jmx server.", getFieldFromOutput(result, "description").get());

        counter.increment();

        final String updated = getCurrentState(expectedMetricName);
        assertEquals("1", getFieldFromOutput(updated, "count").get());
    }

    private String getCurrentState(final String metricName) throws MalformedObjectNameException, MBeanException, InstanceNotFoundException, ReflectionException {
        return (String) mbeanServer.invoke(ObjectName.getInstance("com.blispay.metrics:type=BpJmxReporter"), "getMetric",
                new Object[]{metricName}, new String[]{String.class.getName()});
    }

    private Optional<String> getFieldFromOutput(final String raw, final String key) {
        return new ArrayList<>(Arrays.asList(raw.split(",")))
                .stream()
                .filter((keyVal) -> keyVal.split("=")[0].trim().equals(key))
                .map((keyval) -> keyval.split("=")[1])
                .findFirst();
    }

}