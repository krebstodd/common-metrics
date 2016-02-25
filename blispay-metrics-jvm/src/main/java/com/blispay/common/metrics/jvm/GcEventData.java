package com.blispay.common.metrics.jvm;

public class GcEventData {

    // This is hard coded for now, we'll have to pull it out into a property if we change our strategy.
    private final String collectorStrategy = "CCM";

    private final String action;

    private final String cause;

    private final String name;

    private final Long duration;

    private final Long startTime;

    private final Long endTime;

    private final Long preGcNewGen;
    private final Long postGcNewGen;

    private final Long preGcSurvivor;
    private final Long postGcSurvivor;

    private final Long preGcOldGen;
    private final Long postGcOldGen;

    /**
     * Information about a garbage collection event.
     *
     * @param action The action performed.
     * @param cause The cause of the collection.
     * @param name The name of the collector.
     * @param duration The time, in milliseconds, the collection took.
     * @param startTime The time, in milliseconds, since the JVM started at start of collection.
     * @param endTime The time, in milliseconds, since the JVM started at end of collection.
     * @param preGcNewGen Size, in bytes, of the new generation pre-collection.
     * @param postGcNewGen Size, in bytes, of the new generation post-collection.
     * @param preGcSurvivor Size, in bytes, of the survivor space pre-collection.
     * @param postGcSurvivor Size, in bytes, of the survivor space post-collection.
     * @param preGcOldGen Size, in bytes, of the old generation pre-collection.
     * @param postGcOldGen Size, in bytes, of the old generation post-collection.
     */
    GcEventData(final String action, final String cause, final String name, final Long duration, final Long startTime, final Long endTime,
                final Long preGcNewGen, final Long postGcNewGen, final Long preGcSurvivor, final Long postGcSurvivor, final Long preGcOldGen, final Long postGcOldGen) {

        this.action = action;
        this.cause = cause;
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;

        this.preGcNewGen = preGcNewGen;
        this.postGcNewGen = postGcNewGen;
        this.preGcSurvivor = preGcSurvivor;
        this.postGcSurvivor = postGcSurvivor;
        this.preGcOldGen = preGcOldGen;
        this.postGcOldGen = postGcOldGen;
    }

    public String getCollectorStrategy() {
        return collectorStrategy;
    }

    public String getAction() {
        return action;
    }

    public String getCause() {
        return cause;
    }

    public String getName() {
        return name;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public Long getPreGcNewGen() {
        return preGcNewGen;
    }

    public Long getPostGcNewGen() {
        return postGcNewGen;
    }

    public Long getPreGcSurvivor() {
        return preGcSurvivor;
    }

    public Long getPostGcSurvivor() {
        return postGcSurvivor;
    }

    public Long getPreGcOldGen() {
        return preGcOldGen;
    }

    public Long getPostGcOldGen() {
        return postGcOldGen;
    }

    public static class Builder {

        private String action;
        private String cause;
        private String name;
        private Long duration;
        private Long startTime;
        private Long endTime;
        private Long preGcNewGen;
        private Long postGcNewGen;
        private Long preGcSurvivor;
        private Long postGcSurvivor;
        private Long preGcOldGen;
        private Long postGcOldGen;

        public Builder action(final String action) {
            this.action = action;
            return this;
        }

        public Builder cause(final String cause) {
            this.cause = cause;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder durationMillis(final Long duration) {
            this.duration = duration;
            return this;
        }

        public Builder startTime(final Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(final Long endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * Size pre and post collection of new generation mem pool.
         * @param preGc Size in bytes pre collection.
         * @param postGc Size in bytes post collection.
         * @return This builder.
         */
        public Builder newGen(final Long preGc, final Long postGc) {
            this.preGcNewGen = preGc;
            this.postGcNewGen = postGc;
            return this;
        }

        /**
         * Size pre and post collection of survivor space mem pool.
         * @param preGc Size in bytes pre collection.
         * @param postGc Size in bytes post collection.
         * @return This builder.
         */
        public Builder survivor(final Long preGc, final Long postGc) {
            this.preGcSurvivor = preGc;
            this.postGcSurvivor = postGc;
            return this;
        }

        /**
         * Size pre and post collection of old generation mem pool.
         * @param preGc Size in bytes pre collection.
         * @param postGc Size in bytes post collection.
         * @return This builder.
         */
        public Builder oldGen(final Long preGc, final Long postGc) {
            this.preGcOldGen = preGc;
            this.postGcOldGen = postGc;
            return this;
        }

        /**
         * Build a new GcEventData object based on the currently configured builder.
         * @return Immutable GcEventData.
         */
        public GcEventData build() {
            return new GcEventData(action, cause, name, duration, startTime, endTime,
                    preGcNewGen, postGcNewGen, preGcSurvivor, postGcSurvivor, preGcOldGen, postGcOldGen);
        }

    }
}
