package com.blispay.common.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

class BpJmxReporter implements BpMetricReporter {

    private final MBeanServer mBeanServer;

    public BpJmxReporter() {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public void registerMetric(final BpMetric metric) {
        MetricMBean bean = null;

        if (metric instanceof BpCounter) {
            bean = new JmxCounter((BpCounter) metric);
        } else if (metric instanceof BpHistogram) {
            bean = new JmxHistogram((BpHistogram) metric);
        } else if (metric instanceof BpMeter) {
            bean = new JmxMeter((BpMeter) metric);
        } else if (metric instanceof BpTimer) {
            bean = new JmxTimer((BpTimer) metric);
        }

        try {
            mBeanServer.registerMBean(bean, bean.objectName());
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterMetric(final String metric) {
        try {
            mBeanServer.unregisterMBean(ObjectName.getInstance(metric));
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

    private static ObjectName getName(final BpMetric metric) {
        try {
            return ObjectName.getInstance(metric.getName());
        } catch (MalformedObjectNameException e) {
            // TODO
            e.printStackTrace();
            return null;
        }
    }

    public static interface MetricMBean {
        ObjectName objectName();

        String description();
    }

    public static interface JmxCounterMBean extends MetricMBean {
        long getCount();
    }

    public static interface JmxHistogramMBean extends MetricMBean {
        long getCount();

        long getMin();

        long getMax();

        double getMean();

        double getStdDev();

        double get75thPercentile();

        double get95thPercentile();

        double get98thPercentile();

        double get99thPercentile();

        double get999thPercentile();

        long[] values();
    }

    public static interface JmxMeterMBean extends MetricMBean {
        long getCount();

        double getMeanRate();

        double getOneMinuteRate();

        double getFiveMinuteRate();

        double getFifteenMinuteRate();

        String getRateUnit();
    }

    public static interface JmxTimerMBean extends JmxMeterMBean {
        long getMin();

        long getMax();

        double getMean();

        double getStdDev();

        double get75thPercentile();

        double get95thPercentile();

        double get98thPercentile();

        double get99thPercentile();

        double get999thPercentile();

        long[] values();

        String getDurationUnit();
    }

    private static class JmxCounter implements JmxCounterMBean {

        private final Counter counter;

        private final ObjectName objectName;

        private final String description;

        public JmxCounter(final BpCounter counter) {
            this.counter = counter.getInternalMetric();
            this.objectName = getName(counter);
            this.description = counter.getDescription();
        }

        @Override
        public long getCount() {
            return counter.getCount();
        }

        @Override
        public ObjectName objectName() {
            return objectName;
        }

        @Override
        public String description() {
            return description;
        }
    }

    private static class JmxHistogram implements JmxHistogramMBean {

        private final Histogram histogram;

        private final ObjectName objectName;

        private final String description;

        public JmxHistogram(final BpHistogram histogram) {
            this.histogram = histogram.getInternalMetric();
            this.objectName = getName(histogram);
            this.description = histogram.getDescription();
        }

        @Override
        public long getCount() {
            return histogram.getCount();
        }

        @Override
        public long getMin() {
            return histogram.getSnapshot().getMin();
        }

        @Override
        public long getMax() {
            return histogram.getSnapshot().getMax();
        }

        @Override
        public double getMean() {
            return histogram.getSnapshot().getMean();
        }

        @Override
        public double getStdDev() {
            return histogram.getSnapshot().getStdDev();
        }

        @Override
        public double get75thPercentile() {
            return histogram.getSnapshot().get75thPercentile();
        }

        @Override
        public double get95thPercentile() {
            return histogram.getSnapshot().get95thPercentile();
        }

        @Override
        public double get98thPercentile() {
            return histogram.getSnapshot().get98thPercentile();
        }

        @Override
        public double get99thPercentile() {
            return histogram.getSnapshot().get99thPercentile();
        }

        @Override
        public double get999thPercentile() {
            return histogram.getSnapshot().get999thPercentile();
        }

        @Override
        public long[] values() {
            return histogram.getSnapshot().getValues();
        }

        @Override
        public ObjectName objectName() {
            return objectName;
        }

        @Override
        public String description() {
            return description;
        }
    }

    private static class JmxMeter implements JmxMeterMBean {

        private final Meter meter;

        private final ObjectName objectName;

        private final String description;

        public JmxMeter(final BpMeter meter) {
            this.meter = meter.getInternalMetric();
            this.objectName = getName(meter);
            this.description = meter.getDescription();
        }

        @Override
        public ObjectName objectName() {
            return objectName;
        }

        @Override
        public long getCount() {
            return meter.getCount();
        }

        @Override
        public double getMeanRate() {
            return meter.getMeanRate();
        }

        @Override
        public double getOneMinuteRate() {
            return meter.getOneMinuteRate();
        }

        @Override
        public double getFiveMinuteRate() {
            return meter.getFiveMinuteRate();
        }

        @Override
        public double getFifteenMinuteRate() {
            return meter.getFifteenMinuteRate();
        }

        @Override
        public String getRateUnit() {
            return "TODO";
        }

        @Override
        public String description() {
            return description;
        }
    }

    private static class JmxTimer implements JmxTimerMBean {

        private final Timer timer;

        private final ObjectName objName;

        private final String description;

        public JmxTimer(final BpTimer timer) {
            this.timer = timer.getInternalMetric();
            this.objName = getName(timer);
            this.description = timer.getDescription();
        }

        @Override
        public long getCount() {
            return timer.getCount();
        }

        @Override
        public long getMin() {
            return timer.getSnapshot().getMin();
        }

        @Override
        public long getMax() {
            return timer.getSnapshot().getMax();
        }

        @Override
        public double getMean() {
            return timer.getSnapshot().getMean();
        }

        @Override
        public double getStdDev() {
            return timer.getSnapshot().getStdDev();
        }

        @Override
        public double get75thPercentile() {
            return timer.getSnapshot().get75thPercentile();
        }

        @Override
        public double get95thPercentile() {
            return timer.getSnapshot().get95thPercentile();
        }

        @Override
        public double get98thPercentile() {
            return timer.getSnapshot().get98thPercentile();
        }

        @Override
        public double get99thPercentile() {
            return timer.getSnapshot().get99thPercentile();
        }

        @Override
        public double get999thPercentile() {
            return timer.getSnapshot().get999thPercentile();
        }

        @Override
        public long[] values() {
            return timer.getSnapshot().getValues();
        }

        @Override
        public String getDurationUnit() {
            return "TODO";
        }

        @Override
        public double getMeanRate() {
            return timer.getMeanRate();
        }

        @Override
        public double getOneMinuteRate() {
            return timer.getOneMinuteRate();
        }

        @Override
        public double getFiveMinuteRate() {
            return timer.getFiveMinuteRate();
        }

        @Override
        public double getFifteenMinuteRate() {
            return timer.getFifteenMinuteRate();
        }

        @Override
        public String getRateUnit() {
            return "TODO";
        }

        @Override
        public ObjectName objectName() {
            return objName;
        }

        @Override
        public String description() {
            return description;
        }
    }

}
