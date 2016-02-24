package com.blispay.common.metrics.jvm;

public class GcEventData {

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

    public GcEventData(final String action, final String cause, final String name, final Long duration, final Long startTime, final Long endTime,
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
        
        public Builder newGen(final Long preGc, final Long postGc) {
            this.preGcNewGen = preGc;
            this.postGcNewGen = postGc;
            return this;
        }

        public Builder survivor(final Long preGc, final Long postGc) {
            this.preGcSurvivor = preGc;
            this.postGcSurvivor = postGc;
            return this;
        }

        public Builder oldGen(final Long preGc, final Long postGc) {
            this.preGcOldGen = preGc;
            this.postGcOldGen = postGc;
            return this;
        }

        public GcEventData build() {
            return new GcEventData(action, cause, name, duration, startTime, endTime,
                    preGcNewGen, postGcNewGen, preGcSurvivor, postGcSurvivor, preGcOldGen, postGcOldGen);
        }

    }
}
