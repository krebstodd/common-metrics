package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.metric.MetricProbe;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.utilization.ResourceUtilizationData;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class JvmProbe implements MetricProbe {

    private final MetricService metricService;
    private final MemoryMXBean mxBean;
    private final List<MemoryPoolMXBean> memoryPools;

    public JvmProbe(final MetricService metricService) {
        this(metricService, ManagementFactory.getMemoryMXBean(), ManagementFactory.getMemoryPoolMXBeans());
    }

    public JvmProbe(final MetricService metricService,
                    final MemoryMXBean mxBean,
                    final Collection<MemoryPoolMXBean> memoryPools) {

        this.metricService = metricService;
        this.mxBean = mxBean;
        this.memoryPools = new ArrayList<>(memoryPools);
    }

    @Override
    public void start() {
        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, "heap-utilization", this::heapUtilization);
        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, "non-heap-utilization", this::nonHeapUtilization);
        metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, "total-utilization", this::totalUtilization);

        for (MemoryPoolMXBean pool : memoryPools) {
            metricService.createResourceUtilizationGauge(MetricGroup.RESOURCE_UTILIZATION_MEM, poolMetricName(pool.getName()), poolUtilization(pool));
        }
    }

    public ResourceUtilizationData heapUtilization() {
        final Long min = mxBean.getHeapMemoryUsage().getInit();
        final Long max = mxBean.getHeapMemoryUsage().getMax();
        final Long curr = mxBean.getHeapMemoryUsage().getUsed();
        final Double pct = (double) curr / max;

        return new ResourceUtilizationData(min, max, curr, pct);
    }

    public ResourceUtilizationData nonHeapUtilization() {
        final Long min = mxBean.getNonHeapMemoryUsage().getInit();
        final Long max = mxBean.getNonHeapMemoryUsage().getMax();
        final Long curr = mxBean.getNonHeapMemoryUsage().getUsed();
        final Double pct = (double) curr / max;

        return new ResourceUtilizationData(min, max, curr, pct);
    }

    public ResourceUtilizationData totalUtilization() {
        final Long min = mxBean.getNonHeapMemoryUsage().getInit() + mxBean.getHeapMemoryUsage().getInit();
        final Long max = mxBean.getNonHeapMemoryUsage().getMax() + mxBean.getHeapMemoryUsage().getMax();
        final Long curr = mxBean.getNonHeapMemoryUsage().getUsed() + mxBean.getHeapMemoryUsage().getUsed();
        final Double pct = (double) curr / max;

        return new ResourceUtilizationData(min, max, curr, pct);
    }

    private Supplier<ResourceUtilizationData> poolUtilization(final MemoryPoolMXBean poolBean) {
        return () -> {
            final Long min = mxBean.getNonHeapMemoryUsage().getInit() + mxBean.getHeapMemoryUsage().getInit();
            final Long max = mxBean.getNonHeapMemoryUsage().getMax() + mxBean.getHeapMemoryUsage().getMax();
            final Long curr = mxBean.getNonHeapMemoryUsage().getUsed() + mxBean.getHeapMemoryUsage().getUsed();
            final Double pct = (double) curr / max;

            return new ResourceUtilizationData(min, max, curr, pct);
        };
    }

    private static String poolMetricName(final String poolName) {
        return poolName.toLowerCase().replace(" ", "-") + "-pool-utilization";
    }

}
