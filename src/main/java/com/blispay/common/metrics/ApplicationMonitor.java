package com.blispay.common.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ApplicationMonitor {

    /**
     * Standardized metric namespace for response times.
     */
    public static final String RESPONSE_TIME = "response-time";

    /**
     * Standardized jetty namespace.
     */
    public static final String JETTY = "jetty";

    /**
     * Standardized namespace for an open connection thread.
     */
    public static final String CONNECTION = "connection";

    /**
     * Metric namespace that should be used for database queries.
     */
    public static final String DB_QUERY = "db-query";

    /**
     * Sets a global singleton variable to track if vertx monitoring has been configured.
     */
    public static final AtomicBoolean vertxMonitoringConfigured = new AtomicBoolean(false);

    /**
     * Sets a global singleton variable to track if the jetty server metrics have been set up.
     */
    public static final AtomicBoolean jettyServerConfigured = new AtomicBoolean(false);

    /**
     * The base registry that all metrics will be filed under. This is required so that other frameworks
     * that integrate with dropwizard can reference the shared metric registry we'll create.
     */
    public static final String BASE_REGISTRY = "blispay";

    protected static final MetricRegistry registry = SharedMetricRegistries.getOrCreate(BASE_REGISTRY);

    //CHECK_OFF: StaticVariableName
    private static Optional<Slf4jReporter> slf4jReporter = Optional.empty();

    private static Optional<ConsoleReporter> consoleReporter = Optional.empty();

    private static Optional<JmxReporter> jmxReporter = Optional.empty();

    private static AtomicBoolean jvmMonitoringRunning = new AtomicBoolean(false);
    //CHECK_ON: StaticVariableName

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationMonitor.class);

    protected static <T> T[] extendVarArgs(final T[] base, final T... additional) {
        final int aLen = base.length;
        final int bLen = additional.length;

        @SuppressWarnings("unchecked")
        final T[] c = (T[]) Array.newInstance(base.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(base, 0, c, 0, aLen);
        System.arraycopy(additional, 0, c, aLen, bLen);

        return c;
    }

    /**
     * Start an slf4j reporter for the provided logger name. The slf4j logger will log to the appender linked
     * to the logger name at hte provided sample rate.
     *
     * @param loggerName Slf4j appender name.
     * @param sampleRate Rate at which we should sample the applications configured metric sets.
     * @param sampleRateUnit The time unit associated with the sample rate.
     */
    public static synchronized void startSlf4jReporter(final String loggerName,
                                          final Integer sampleRate,
                                          final TimeUnit sampleRateUnit) {
        LOG.info("Starting slf4j application monitoring reporter for logger {} sampling at {} samples per {}.", loggerName, sampleRate, sampleRateUnit.toString());

        final Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LoggerFactory.getLogger(loggerName))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .withLoggingLevel(Slf4jReporter.LoggingLevel.INFO)
                .build();

        reporter.start(sampleRate, sampleRateUnit);
        slf4jReporter = Optional.of(reporter);
    }

    /**
     * Start logging application performance to the console. Should only be turned on for development as this will
     * create a lot of console logs.
     *
     * @param sampleRate Rate at which we should sample the applications configured metric sets.
     * @param sampleRateUnit The time unit associated with the sample rate.
     */
    public static synchronized void startConsoleReporter(final Integer sampleRate,
                                                         final TimeUnit sampleRateUnit) {
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(sampleRate, sampleRateUnit);
        consoleReporter = Optional.of(reporter);
    }

    /**
     * Start monitoring various jvm parameters (memory usage, gc, thread state, etc.).
     */
    public static synchronized void startJvmMonitoring() {
        if (jvmMonitoringRunning.compareAndSet(false, true)) {
            registry.registerAll(new MemoryUsageGaugeSet());
            registry.registerAll(new GarbageCollectorMetricSet());
            registry.registerAll(new ThreadStatesGaugeSet());
        }
    }

    /**
     * Start a jmx bean that exposes all of the metric sets configured for the application.
     */
    public static synchronized void startJmxReporter() {
        final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();

        jmxReporter = Optional.of(reporter);
    }

    /**
     * Turn off the slf4j reporter if one has been configured.
     */
    public static synchronized void stopSlf4jReporter() {
        LOG.info("Stopping slf4j application monitoring reporter.");
        slf4jReporter.ifPresent(Slf4jReporter::stop);
    }

    /**
     * Turn off the console reporter if one has been configured.
     */
    public static synchronized void stopConsoleReporter() {
        consoleReporter.ifPresent(ConsoleReporter::stop);
    }

    /**
     * Turn off the jmx reporter if one has been configured.
     */
    public static synchronized void stopJmxReporter() {
        jmxReporter.ifPresent(JmxReporter::stop);
    }

    /**
     * Shut down the application monitor and remove all listeners/metrics.
     */
    public static synchronized void shutdown() {
        slf4jReporter.ifPresent(Slf4jReporter::stop);
        consoleReporter.ifPresent(ConsoleReporter::stop);
        jmxReporter.ifPresent(JmxReporter::stop);
        jvmMonitoringRunning.set(false);
        vertxMonitoringConfigured.set(false);
        registry.removeMatching(MetricFilter.ALL);
    }

    @FunctionalInterface
    public interface MetricResolver {
        long done();
    }

    @FunctionalInterface
    public interface Notifier {
        void done();
    }
}
