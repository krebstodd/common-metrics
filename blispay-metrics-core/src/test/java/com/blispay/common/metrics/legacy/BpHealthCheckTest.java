package com.blispay.common.metrics.legacy;

import com.blispay.common.metrics.AbstractMetricsTest;

// CHECK_OFF: MultipleStringLiterals
public class BpHealthCheckTest extends AbstractMetricsTest {

//    @Test
//    public void testGenericHealthCheck() {
//
//        final AtomicBoolean currentHealth = new AtomicBoolean(true);
//        final Supplier<BpHealthCheck.Result> healthProbe = () -> {
//            if (currentHealth.get()) {
//                return BpHealthCheck.Result.healthy();
//            } else {
//                return BpHealthCheck.Result.unhealthy("UNHEALTHY");
//            }
//        };
//
//        final BpHealthCheck healthCheck = metricService.createHealthCheck(new BusinessMetricName("healthCheck", "isHealthy"), MetricClass.businessEvent(), healthProbe);
//
//        assertTrue(healthCheck.checkHealth().isHealthy());
//        assertNull(healthCheck.checkHealth().getMessage());
//        assertNull(healthCheck.checkHealth().getThrowable());
//        currentHealth.set(false);
//        assertFalse(healthCheck.checkHealth().isHealthy());
//        assertEquals("UNHEALTHY", healthCheck.checkHealth().getMessage());
//    }

}
// CHECK_OFF: MultipleStringLiterals
