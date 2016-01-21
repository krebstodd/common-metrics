package com.blispay.common.metrics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.probe.BpMetricProbe;
import org.slf4j.LoggerFactory;

public class LogbackProbe extends BpMetricProbe {

    private BpMetricService metricService;

    /**
     * Probe for profiling logback.
     *
     * @param metricService Metric service to register probe on.
     */
    public LogbackProbe(final BpMetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    protected void startProbe() {
        final LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
        final Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);

        final Appender metricsAppender = new Appender();
        metricsAppender.setContext(root.getLoggerContext());
        metricsAppender.start();
        root.addAppender(metricsAppender);
    }

    @Override
    protected org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger(LogbackProbe.class);
    }

    public final class Appender extends UnsynchronizedAppenderBase<ILoggingEvent> {

        private BpMeter all = metricService.createMeter(getClass(), "logback-all", "Meter for all logback log types.");

        private BpMeter trace = metricService.createMeter(getClass(), "logback-trace", "Meter for logback trace log types.");

        private BpMeter debug = metricService.createMeter(getClass(), "logback-debug", "Meter for logback debug log types.");

        private BpMeter info = metricService.createMeter(getClass(), "logback-info", "Meter for logback info log types.");

        private BpMeter warn = metricService.createMeter(getClass(), "logback-warn", "Meter for logback warning log types.");

        private BpMeter error = metricService.createMeter(getClass(), "logback-error", "Meter for logback error log types.");

        /**
         * Create a new appender to monitor logging events.
         */
        public Appender() {
            setName(Appender.class.getName());

            // Don't log an event every time a logger logs, creates feedback loop resulting in a stack overflow.
            all.enableEventRecording(Boolean.FALSE);
            trace.enableEventRecording(Boolean.FALSE);
            debug.enableEventRecording(Boolean.FALSE);
            info.enableEventRecording(Boolean.FALSE);
            warn.enableEventRecording(Boolean.FALSE);
            error.enableEventRecording(Boolean.FALSE);
        }

        @Override
        protected void append(final ILoggingEvent event) {
            all.mark();
            switch (event.getLevel().toInt()) {
                case Level.TRACE_INT:
                    trace.mark();
                    break;
                case Level.DEBUG_INT:
                    debug.mark();
                    break;
                case Level.INFO_INT:
                    info.mark();
                    break;
                case Level.WARN_INT:
                    warn.mark();
                    break;
                case Level.ERROR_INT:
                    error.mark();
                    break;
                default:
                    break;
            }
        }
    }
}
