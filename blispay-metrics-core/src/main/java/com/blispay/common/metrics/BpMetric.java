package com.blispay.common.metrics;

import com.codahale.metrics.Metric;

import java.time.Instant;

public abstract class BpMetric implements Metric {

    private final String name;

    private final String description;

    private final String rateUnit = "PER_SECOND";

    private final String durationUnit = "NANOSECOND";

    public BpMetric(final String name, final String description) {
        this.name = name;
        this.description = description;
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

    public abstract Sample sample();

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
         * Pretty print the current sample to a readable string of comma separated key value pairs.
         *
         * @param prettyPrint True if each key value pair should be on a new line.
         * @return string representation of sample.
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
