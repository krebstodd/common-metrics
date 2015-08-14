package com.blispay.common.metrics.reporter;

import com.blispay.common.metrics.BpMetric;
import com.blispay.common.metrics.BpMetricConsumer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BpJmxReporter implements BpMetricConsumer {

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
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerMetric(final BpMetric metric) {
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

//    private static JmxMetricMBean toMbean(final BpMetric metric) {
//        JmxMetricMBean bean = null;
//
//        if (metric instanceof BpCounter) {
//            bean = new JmxCounter((BpCounter) metric);
//        } else if (metric instanceof BpHistogram) {
//            bean = new JmxHistogram((BpHistogram) metric);
//        } else if (metric instanceof BpMeter) {
//            bean = new JmxMeter((BpMeter) metric);
//        } else if (metric instanceof BpTimer) {
//            bean = new JmxTimer((BpTimer) metric);
//        }
//
//        return bean;
//    }
//
//    public static interface MetricMBean {
//        ObjectName objectName();
//
//        String name();
//
//        String description();
//    }
//
//    public static class JmxMetricMBean implements MetricMBean {
//
//        private final ObjectName objectName;
//
//        private final String description;
//
//        private final String name;
//
//        public JmxMetricMBean(final BpMetric metric) {
//            this.objectName = getName(metric);
//            this.description = metric.getDescription();
//            this.name = metric.getName();
//        }
//
//        @Override
//        public ObjectName objectName() {
//            return null;
//        }
//
//        @Override
//        public String name() {
//            return null;
//        }
//
//        @Override
//        public String description() {
//            return null;
//        }
//    }
//
//    public static interface JmxCounterMBean extends MetricMBean {
//        long getCount();
//    }
//
//    public static interface JmxHistogramMBean extends MetricMBean {
//        long getCount();
//
//        long getMin();
//
//        long getMax();
//
//        double getMean();
//
//        double get75thPercentile();
//
//        double get95thPercentile();
//
//        double get98thPercentile();
//
//        double get99thPercentile();
//
//        double get999thPercentile();
//
//        long[] values();
//    }
//
//    public static interface JmxMeterMBean extends MetricMBean {
//        long getCount();
//
//        double getMeanRate();
//
//        double getOneMinuteRate();
//
//        double getFiveMinuteRate();
//
//        double getFifteenMinuteRate();
//
//        String getRateUnit();
//    }
//
//    public static interface JmxTimerMBean extends JmxMeterMBean {
//        long getMin();
//
//        long getMax();
//
//        double getMean();
//
//        double get75thPercentile();
//
//        double get95thPercentile();
//
//        double get98thPercentile();
//
//        double get99thPercentile();
//
//        double get999thPercentile();
//
//        long[] values();
//
//        String getDurationUnit();
//    }
//
//    private static class JmxCounter extends JmxMetricMBean implements JmxCounterMBean {
//
//        private final BpCounter counter;
//
//        public JmxCounter(final BpCounter counter) {
//            super(counter);
//            this.counter = counter;
//        }
//
//        @Override
//        public long getCount() {
//            return counter.getCount();
//        }
//    }
//
//    private static class JmxHistogram extends JmxMetricMBean implements JmxHistogramMBean {
//
//        private final BpHistogram histogram;
//
//        public JmxHistogram(final BpHistogram histogram) {
//            super(histogram);
//            this.histogram = histogram;
//        }
//
//        @Override
//        public long getCount() {
//            return histogram.getCount();
//        }
//
//        @Override
//        public long getMin() {
//            return histogram.getMin();
//        }
//
//        @Override
//        public long getMax() {
//            return histogram.getMax();
//        }
//
//        @Override
//        public double getMean() {
//            return histogram.getMean();
//        }
//
//        @Override
//        public double get75thPercentile() {
//            return histogram.get75thPercentile();
//        }
//
//        @Override
//        public double get95thPercentile() {
//            return histogram.get95thPercentile();
//        }
//
//        @Override
//        public double get98thPercentile() {
//            return histogram.get98thPercentile();
//        }
//
//        @Override
//        public double get99thPercentile() {
//            return histogram.get99thPercentile();
//        }
//
//        @Override
//        public double get999thPercentile() {
//            return histogram.get999thPercentile();
//        }
//
//        @Override
//        public long[] values() {
//            return histogram.getValues();
//        }
//
//    }
//
//    private static class JmxMeter extends JmxMetricMBean implements JmxMeterMBean {
//
//        private final BpMeter meter;
//
//        public JmxMeter(final BpMeter meter) {
//            super(meter);
//            this.meter = meter;
//        }
//
//        @Override
//        public long getCount() {
//            return meter.getCount();
//        }
//
//        @Override
//        public double getMeanRate() {
//            return meter.getMeanRate();
//        }
//
//        @Override
//        public double getOneMinuteRate() {
//            return meter.getOneMinuteRate();
//        }
//
//        @Override
//        public double getFiveMinuteRate() {
//            return meter.getFiveMinuteRate();
//        }
//
//        @Override
//        public double getFifteenMinuteRate() {
//            return meter.getFifteenMinuteRate();
//        }
//
//        @Override
//        public String getRateUnit() {
//            return "TODO";
//        }
//
//    }
//
//    private static class JmxTimer extends JmxMetricMBean implements JmxTimerMBean {
//
//        private final BpTimer timer;
//
//        public JmxTimer(final BpTimer timer) {
//            super(timer);
//            this.timer = timer;
//        }
//
//        @Override
//        public long getCount() {
//            return timer.getCount();
//        }
//
//        @Override
//        public long getMin() {
//            return timer.getMin();
//        }
//
//        @Override
//        public long getMax() {
//            return timer.getMax();
//        }
//
//        @Override
//        public double getMean() {
//            return timer.getMean();
//        }
//
//        @Override
//        public double get75thPercentile() {
//            return timer.get75thPercentile();
//        }
//
//        @Override
//        public double get95thPercentile() {
//            return timer.get95thPercentile();
//        }
//
//        @Override
//        public double get98thPercentile() {
//            return timer.get98thPercentile();
//        }
//
//        @Override
//        public double get99thPercentile() {
//            return timer.get99thPercentile();
//        }
//
//        @Override
//        public double get999thPercentile() {
//            return timer.get999thPercentile();
//        }
//
//        @Override
//        public long[] values() {
//            return timer.getValues();
//        }
//
//        @Override
//        public String getDurationUnit() {
//            return "TODO";
//        }
//
//        @Override
//        public double getMeanRate() {
//            return timer.getMeanRate();
//        }
//
//        @Override
//        public double getOneMinuteRate() {
//            return timer.getOneMinuteRate();
//        }
//
//        @Override
//        public double getFiveMinuteRate() {
//            return timer.getFiveMinuteRate();
//        }
//
//        @Override
//        public double getFifteenMinuteRate() {
//            return timer.getFifteenMinuteRate();
//        }
//
//        @Override
//        public String getRateUnit() {
//            return "TODO";
//        }
//    }

}
