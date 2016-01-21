package com.blispay.common.metrics.metric;

import com.blispay.common.metrics.report.BpEventRecordingService;
import com.blispay.common.metrics.report.NoOpEventRecordingService;
import com.blispay.common.metrics.util.ImmutablePair;
import com.blispay.common.metrics.util.RecordableEvent;
import com.codahale.metrics.Metric;

import java.time.Instant;
import java.util.function.Function;

public abstract class BpMetric<T> implements Metric {

    private static final Boolean DEFAULT_RECORD_EVENTS = Boolean.FALSE;

    private static final String rateUnit = "PER_SECOND";

    private static final String durationUnit = "NANOSECOND";

    private final String name;

    private final String description;

    private BpEventRecordingService eventRecordingService;

    private Boolean recordEvents;
    private Function<EventSample<T>, RecordableEvent.Level> recordLevelFn;

    public BpMetric(final String name, final String description) {
        this(name, description, DEFAULT_RECORD_EVENTS);
    }

    /**
     * Create a new bp metric instance with default event recording set up.
     *
     * @param name Name of the metric.
     * @param description Brief description of the metric.
     * @param enableEventRecording Enable the recording of each sampling event.
     */
    public BpMetric(final String name, final String description, final Boolean enableEventRecording) {
        this.name = name;
        this.description = description;
        this.eventRecordingService = new NoOpEventRecordingService();
        this.recordEvents = enableEventRecording;
        this.recordLevelFn = (in) -> RecordableEvent.Level.INFO;
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

    public void setEventRecordingService(final BpEventRecordingService service) {
        this.eventRecordingService = service;
    }

    public void enableEventRecording(final Boolean recordEvents) {
        this.recordEvents = recordEvents;
    }

    public void setEventRecordLevelFn(final Function<EventSample<T>, RecordableEvent.Level> recordLevelFn) {
        this.recordLevelFn = recordLevelFn;
    }

    protected void recordEvent(final EventSample<T> sample) {
        if (recordEvents) {
            eventRecordingService.recordEvent(new RecordableEvent(recordLevelFn.apply(sample), sample));
        }
    }

    public abstract Sample aggregateSample();

    public static class Sample {

        private final Instant sampleTime;

        private final String metricName;

        private final ImmutablePair[] sampleData;

        private final SampleType type;

        protected Sample(final String metricName, final ImmutablePair[] data, final SampleType type) {
            this.sampleTime = Instant.now();
            this.metricName = metricName;
            this.sampleData = data;
            this.type = type;
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

        public SampleType getType() {
            return type;
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

            sb.append("sampleType=").append(type.name()).append(",");

            if (prettyPrint) {
                sb.append("\n");
            }

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

    public static class EventSample<T> extends Sample {

        private final T sampleValue;

        protected EventSample(final String metricName, final ImmutablePair[] data,
                              final SampleType type, final T primarySample) {

            super(metricName, data, type);
            this.sampleValue = primarySample;
        }

        public T getSampleState() {
            return sampleValue;
        }

    }

    public static enum SampleType {
        /**
         * An event has occurred (a statistic sample has been added to the metric).
         */
        EVENT,
        /**
         * A current snapshot of the metric in recent history.
         */
        AGGREGATE
    }

}
