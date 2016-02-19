package com.blispay.common.metrics;

import com.blispay.common.metrics.model.BpMeter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

// CHECK_OFF: MagicNumber
public class LogbackProbeTest {

    private final MetricService metricService = MetricService.globalInstance();

    @Test
    public void testLogbackProbe() {
        final LogbackProbe probe = new LogbackProbe(metricService);
        probe.start();

        final Logger log = LoggerFactory.getLogger(LogbackProbeTest.class);

        log.info("test");
        log.info("test2");

        log.warn("warning");

        log.error("error");

        log.debug("debug");

        assertEquals(5L, getCount("all"));
        assertEquals(2L, getCount("info"));
        assertEquals(1L, getCount("warn"));
        assertEquals(1L, getCount("error"));
        assertEquals(1L, getCount("debug"));
    }

    private long getCount(final String type) {
        return ((BpMeter) metricService.getMetric(LogbackProbe.Appender.class, "logback-" + type)).getCount();
    }
}
// CHECK_ON: MagicNumber
