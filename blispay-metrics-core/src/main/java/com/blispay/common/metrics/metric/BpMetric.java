package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.report.BpEventEmitter;
import com.blispay.common.metrics.report.BpEventService;
import com.blispay.common.metrics.report.NoOpEventReportingService;
import com.blispay.common.metrics.util.ImmutablePair;
import com.blispay.common.metrics.util.MetricEvent;
import com.blispay.common.metrics.util.RecordableEvent;
import com.codahale.metrics.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.function.Function;

public abstract class BpMetric<T> implements Metric, BpEventEmitter {

    private static final Logger LOG = LoggerFactory.getLogger(BpMetric.class);

    private static final Boolean DEFAULT_PUBLISH_EVENTS = Boolean.FALSE;

    private static final String rateUnit = "PER_SECOND";
    private static final String durationUnit = "NANOSECOND";

    private final Class<?> owner;
    private final String name;
    private final String description;

    private BpEventService eventService;

    private Boolean enableEventPublishing;
    private Function<MetricEvent<T>, RecordableEvent.Level> recordLevelFn;

    public BpMetric(final Class<?> owner, final String name, final String description) {
        this(owner, name, description, DEFAULT_PUBLISH_EVENTS);
    }

    /**
     * Create a new bp metric instance with default event recording set up.
     *
     * @param owner The class that owns this particular metric.
     * @param name Name of the metric.
     * @param description Brief description of the metric.
     * @param enableEventPublishing Enable the recording of each sampling event.
     */
    public BpMetric(final Class<?> owner, final String name, final String description, final Boolean enableEventPublishing) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.eventService = new NoOpEventReportingService();
        this.enableEventPublishing = enableEventPublishing;
        this.recordLevelFn = (in) -> RecordableEvent.Level.INFO;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRateUnit() {
        return rateUnit;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setEventService(final BpEventService service) {
        this.eventService = service;
    }

    public void enableEventPublishing(final Boolean enableEventPublishing) {
        this.enableEventPublishing = enableEventPublishing;
    }

    public void setEventRecordLevelFn(final Function<MetricEvent<T>, RecordableEvent.Level> recordLevelFn) {
        this.recordLevelFn = recordLevelFn;
    }

    protected void publishEvent(final String eventKey, final T value) {
        try {
            if (enableEventPublishing) {
                final MetricEvent<T> metricEvent = new MetricEvent<>(owner, name, eventKey, value);
                eventService.acceptEvent(new RecordableEvent<>(metricEvent, recordLevelFn.apply(metricEvent)));
            }

        // CHECK_OFF: IllegalCatch
        } catch (Exception ex) {
            LOG.error("Caught exception attempting to publish event metric", ex);
        }
        // CHECK_ON: IllegalCatch

    }

    public abstract Sample aggregateSample();

    public static class Sample {

        private final Instant sampleTime;

        private final String metricName;

        private final ImmutablePair[] sampleData;

        protected Sample(final String metricName, final ImmutablePair[] data) {
            this.sampleTime = Instant.now();
            this.metricName = metricName;
            this.sampleData = data;
        }

        public Instant getSampleTime() {
            return sampleTime;
        }

        public String getNamespace() {
            return metricName;
        }

        /**
         * Get a specific metric attribute by it's name.
         *
         * @param key The key name of the attribute on the metric.
         * @return The value of the metric attribute if it exists.
         */
        public Object getAttribute(final String key) {
            for (int i = 0; i < sampleData.length; i++) {
                if (sampleData[i].getKey().equals(key)) {
                    return sampleData[i].getVal();
                }
            }
            return null;
        }

        public ImmutablePair[] getSampleData() {
            return sampleData;
        }

        @Override
        public String toString() {
            return toString(false);
        }

        /**
         * Pretty print theaggregateSamplent sample to a readable string of comma separated key value pairs.
         *
         * @param prettyPrint True if each key value pair should be on a new line.
         * @return string representaggregateSampleof sample.
         */
        // CHECK_OFF: MultipleStringLiterals
        // CHECK_OFF: NPathComplexity
        public String toString(final Boolean prettyPrint) {
            final StringBuilder sb = new StringBuilder();

            final ImmutablePair[] sample = getSampleData();
            ImmutablePair current;
            for (int i = 0; i < sample.length; i++) {
                current = sample[i];
                sb.append(current.getKey()).append("=").append(current.getVal()).append(",");

                if (prettyPrint) {
                    sb.append("\n");
                }
            }

            String str = sb.toString();

            if (str.endsWith("\n")) {
                str = str.substring(0, str.length() - 1);
            }

            if (str.endsWith(",")) {
                return str.substring(0, str.length() - 1);
            } else {
                return str;
            }
        }
        // CHECK_ON: MultipleStringLiterals
        // CHECK_ON: NPathComplexity

    }

}
