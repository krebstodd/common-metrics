package com.blispay.common.metrics.report;

import com.blispay.common.metrics.metric.BpMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class BpJmxReporter extends BpMetricReporter {

    private static final Logger LOG = LoggerFactory.getLogger(BpJmxReporter.class);

    private final MBeanServer beanServer;

    private final JmxMetricServiceMBean serviceMBean;

    /**
     * Create a new metric consumer to expose currently tracked metrics via jmx.
     */
    public BpJmxReporter() {
        super();
        beanServer = ManagementFactory.getPlatformMBeanServer();
        serviceMBean = new JmxMetricService();

        try {
            beanServer.registerMBean(serviceMBean, ObjectName.getInstance("com.blispay.metrics:type=BpJmxReporter"));
        } catch (JMException ex) {
            LOG.error("Unable to register metrics mbean with jmx server.", ex);
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void report() {
    }

    public interface JmxMetricServiceMBean {
        String getMetric(String metricName);
    }

    public class JmxMetricService implements JmxMetricServiceMBean {

        @Override
        public String getMetric(final String metricName) {
            final BpMetric.Sample sample = BpJmxReporter.this.sampleMetrics().get(metricName);
            if (sample != null) {
                return sample.toString(true);
            } else {
                return "Unable to locate metric.";
            }
        }

    }
}
