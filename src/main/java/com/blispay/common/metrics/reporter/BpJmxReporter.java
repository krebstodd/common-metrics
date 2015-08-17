package com.blispay.common.metrics.reporter;

import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BpJmxReporter implements BpMetricConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(BpJmxReporter.class);

    private static final Map<String, BpMetric> metrics = new ConcurrentHashMap<>();

    private final MBeanServer beanServer;

    private final JmxMetricServiceMBean serviceMBean;

    /**
     * Create a new metric consumer to expose currently tracked metrics via jmx.
     */
    public BpJmxReporter() {
        beanServer = ManagementFactory.getPlatformMBeanServer();
        serviceMBean = new JmxMetricService();

        try {
            beanServer.registerMBean(serviceMBean, ObjectName.getInstance("com.blispay.metrics:type=BpJmxReporter"));
        } catch (JMException ex) {
            LOG.error("Unable to register metrics mbean with jmx server.", ex);
        }
    }

    @Override
    public void registerMetric(final BpMetric metric) {

        System.out.println(metric.getName());
        metrics.put(metric.getName(), metric);
    }

    @Override
    public void unregisterMetric(final String metric) {
        metrics.remove(metric);
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

    public interface JmxMetricServiceMBean {
        String getMetric(String metricName);
    }

    public static class JmxMetricService implements JmxMetricServiceMBean {

        @Override
        public String getMetric(final String metricName) {
            final BpMetric metric = metrics.get(metricName);
            if (metric != null) {
                return metric.sample().toString(true);
            } else {
                return "Unable to locate metric.";
            }
        }

    }
}
