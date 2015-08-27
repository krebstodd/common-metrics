package com.blispay.common.metrics;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

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

        final BpHealthCheck healthCheck = metricService.createHealthCheck(BpHealthCheckTest.class, "currentHealth", "Current health of the bp health check test", healthProbe);

        assertTrue((Boolean) healthCheck.sample().getAttribute("healthy"));
        assertNull(healthCheck.sample().getAttribute("message"));
        assertNull(healthCheck.sample().getAttribute("throwable"));
        currentHealth.set(false);
        assertFalse((Boolean) healthCheck.sample().getAttribute("healthy"));
        assertEquals("UNHEALTHY", healthCheck.sample().getAttribute("message"));
    }

}
// CHECK_OFF: MultipleStringLiterals
