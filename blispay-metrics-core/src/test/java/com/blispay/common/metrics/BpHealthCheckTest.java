package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpHealthCheck;
import com.blispay.common.metrics.metric.BusinessMetricName;
import com.blispay.common.metrics.metric.MetricClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// CHECK_OFF: MultipleStringLiterals
public class BpHealthCheckTest extends AbstractMetricsTest {

    @Test
    public void testGenericHealthCheck() {

        final AtomicBoolean currentHealth = new AtomicBoolean(true);
        final Supplier<BpHealthCheck.Result> healthProbe = () -> {
            if (currentHealth.get()) {
                return BpHealthCheck.Result.healthy();
            } else {
                return BpHealthCheck.Result.unhealthy("UNHEALTHY");
            }
        };

        final BpHealthCheck healthCheck = metricService.createHealthCheck(new BusinessMetricName("healthCheck", "isHealthy"), MetricClass.businessEvent(), healthProbe);

        assertTrue(healthCheck.checkHealth().isHealthy());
        assertNull(healthCheck.checkHealth().getMessage());
        assertNull(healthCheck.checkHealth().getThrowable());
        currentHealth.set(false);
        assertFalse(healthCheck.checkHealth().isHealthy());
        assertEquals("UNHEALTHY", healthCheck.checkHealth().getMessage());
    }

}
// CHECK_OFF: MultipleStringLiterals
