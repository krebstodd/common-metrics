package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.metric.MetricProbe;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JvmProbe implements MetricProbe {

    private static final Logger LOG = LoggerFactory.getLogger(JvmProbe.class);

    private final MetricService metricService;
    private final MemoryMXBean mxBean;
    private final List<MemoryPoolMXBean> memoryPools;
    private final List<GarbageCollectorMXBean> garbageCollectors;
    private final AtomicBoolean isRunning = new AtomicBoolean(Boolean.FALSE);

    private List<GcNotificationListener> gcListeners;

    public JvmProbe(final MetricService metricService) {
        this(metricService, ManagementFactory.getMemoryMXBean(), ManagementFactory.getMemoryPoolMXBeans(), ManagementFactory.getGarbageCollectorMXBeans());
    }

    /**
     * Create a new jvm probe.
     * @param metricService Metric service to report to.
     * @param mxBean Memory mx bean.
     * @param memoryPools Collection of memory pool mx beans.
     */
    public JvmProbe(final MetricService metricService,
                    final MemoryMXBean mxBean,
                    final List<MemoryPoolMXBean> memoryPools,
                    final List<GarbageCollectorMXBean> garbageCollectors) {

        this.metricService = metricService;
        this.mxBean = mxBean;
        this.memoryPools = memoryPools;
        this.garbageCollectors = garbageCollectors;
    }

    @Override
    public void start() {
        System.out.println(">>>");

        if (isRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(">>>");
            LOG.info("Starting JVM metric probe...");

            // Create jvm wide memory utilization metrics
            metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, "heap-utilization", this::heapUtilization);
            metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, "non-heap-utilization", this::nonHeapUtilization);
            metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, "total-utilization", this::totalUtilization);

            // Create memory pool utilization metrics.
            for (MemoryPoolMXBean pool : memoryPools) {
                metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, poolMetricName(pool.getName()), poolUtilization(pool));
            }

            // Add garbage collection listener.
            this.gcListeners = new LinkedList<>();
            for (GarbageCollectorMXBean gc : garbageCollectors) {
                this.gcListeners.add(instrumentGarbageCollector(gc, metricService));
            }

            // Create JVM wide thread guage metrics.
            metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "jvm-active", threadUtilization(JvmProbe::isActive));
            metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_THREADS, "jvm-blocked", threadUtilization(JvmProbe::isBlocked));

            LOG.info("JVM metric probe started.");
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {
            LOG.info("Stopping JVM metric probe...");
            removeGarbageCollectionListeners(this.garbageCollectors.stream().map(gc -> (NotificationEmitter) gc).collect(Collectors.toList()), gcListeners);
            LOG.info("JVM metric probe stopped.");
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Point in time utilization data for the heap memory space allocated to the currently running process.
     * @return utilization data
     */
    public ResourceUtilizationData heapUtilization() {
        final Long min = mxBean.getHeapMemoryUsage().getInit();
        final Long max = mxBean.getHeapMemoryUsage().getMax();
        final Long curr = mxBean.getHeapMemoryUsage().getUsed();
        final Double pct = (double) curr / max;

        return new ResourceUtilizationData(min, max, curr, pct);
    }

    /**
     * Point in time utilization data for the non-heap memory space allocated to the currently running process.
     * @return utilization data
     */
    public ResourceUtilizationData nonHeapUtilization() {
        final Long min = mxBean.getNonHeapMemoryUsage().getInit();
        final Long max = mxBean.getNonHeapMemoryUsage().getMax();
        final Long curr = mxBean.getNonHeapMemoryUsage().getUsed();
        final Double pct = (double) curr / max;

        return new ResourceUtilizationData(min, max, curr, pct);
    }

    /**
     * Point in time utilization data for the total memory space allocated to the currently running process.
     * @return utilization data
     */
    public ResourceUtilizationData totalUtilization() {
        final Long min = mxBean.getNonHeapMemoryUsage().getInit() + mxBean.getHeapMemoryUsage().getInit();
        final Long max = mxBean.getNonHeapMemoryUsage().getMax() + mxBean.getHeapMemoryUsage().getMax();
        final Long curr = mxBean.getNonHeapMemoryUsage().getUsed() + mxBean.getHeapMemoryUsage().getUsed();
        final Double pct = (double) curr / max;

        return new ResourceUtilizationData(min, max, curr, pct);
    }

    /**
     * Point in time utilization data for a specific memory pool.
     *
     * @param poolBean Memory pool mx bean to profile.
     * @return utilization data
     */
    public Supplier<ResourceUtilizationData> poolUtilization(final MemoryPoolMXBean poolBean) {
        return () -> {
            final Long min = poolBean.getUsage().getInit();
            final Long max = poolBean.getUsage().getMax();
            final Long curr = poolBean.getUsage().getUsed();
            final Double pct = (double) curr / max;

            return new ResourceUtilizationData(min, max, curr, pct);
        };
    }

    private static Supplier<ResourceUtilizationData> threadUtilization(final Predicate<Thread> statePredicate) {
        return () -> {
                final Set<Thread> threads = Thread.getAllStackTraces().keySet();
                final int threadCount = threads.size();
                final long runningCount = threads.stream().filter(statePredicate).count();

                return new ResourceUtilizationData(0L, (long) threadCount, runningCount, (double) runningCount / threadCount);
            };
    }

    private static Boolean isActive(final Thread thread) {
        return isBlocked(thread) || thread.getState() == Thread.State.RUNNABLE;
    }

    private static Boolean isBlocked(final Thread thread) {
        return thread.getState() == Thread.State.BLOCKED
                || thread.getState() == Thread.State.WAITING
                || thread.getState() == Thread.State.TIMED_WAITING;
    }

    private static String poolMetricName(final String poolName) {
        return poolName.toLowerCase(Locale.ROOT).replace(" ", "-") + "-pool-utilization";
    }

    private static void removeGarbageCollectionListeners(final List<NotificationEmitter> emitters, final List<GcNotificationListener> gc) {
        emitters.forEach(emitter -> gc.forEach(listener -> {
                try {
                    emitter.removeNotificationListener(listener);
                } catch (ListenerNotFoundException e) {
                    LOG.debug("Emitter does not own this listener, moving on.");
                }
            }));
    }

    private static GcNotificationListener instrumentGarbageCollector(final GarbageCollectorMXBean gc, final MetricService metricService) {
        final NotificationEmitter emitter = (NotificationEmitter) gc;
        final GcNotificationListener gcListener = new GcNotificationListener(metricService.createEventRepository(MetricGroup.RESOURCE_UTILIZATION_GC, "gc"));
        emitter.addNotificationListener(gcListener, new GcNotificationFilter(), null);
        return gcListener;
    }

}
