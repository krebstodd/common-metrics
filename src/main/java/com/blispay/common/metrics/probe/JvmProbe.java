//package com.blispay.common.metrics.probe;
//
//import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
//import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
//import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
//
//public class JvmProbe {
//
//
//
//    private static void instrumentJvmMonitoring() {
//        final Boolean jvmMonitoringEnabled = (Boolean) System.getProperties().getOrDefault("metrics.jvm.enabled", false);
//
//        if (jvmMonitoringEnabled) {
//            service.getRegistry().registerAll(new MemoryUsageGaugeSet());
//            service.getRegistry().registerAll(new GarbageCollectorMetricSet());
//            service.getRegistry().registerAll(new ThreadStatesGaugeSet());
//        }
//    }
//
//}
