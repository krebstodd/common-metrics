package com.blispay.common.metrics.jvm;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.common.base.CaseFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * Class GcEventData.
 */
public class GcEventData {

    // This is hard coded for now, we'll have to pull it out into a property if we change our strategy.
    private final String collectorStrategy = "CCM";

    private final String action;

    private final String cause;

    private final String name;

    private final Long duration;

    private final Long startTime;

    private final Long endTime;

    private final Map<String, Long> prePostFreeMemory;

    /**
     * Information about a garbage collection event.
     *
     * @param action The action performed.
     * @param cause The cause of the collection.
     * @param name The name of the collector.
     * @param duration The time, in milliseconds, the collection took.
     * @param startTime The time, in milliseconds, since the JVM started at start of collection.
     * @param endTime The time, in milliseconds, since the JVM started at end of collection.
     * @param prePostFreeMem Size, in bytes, of memory pools pre and post collection.
     */
    GcEventData(final String action, final String cause, final String name, final Long duration, final Long startTime, final Long endTime, final Map<String, Long> prePostFreeMem) {

        this.action = action;
        this.cause = cause;
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.prePostFreeMemory = prePostFreeMem;
    }

    /**
     * Method getCollectorStrategy.
     *
     * @return return value.
     */
    public String getCollectorStrategy() {
        return collectorStrategy;
    }

    /**
     * Method getAction.
     *
     * @return return value.
     */
    public String getAction() {
        return action;
    }

    /**
     * Method getCause.
     *
     * @return return value.
     */
    public String getCause() {
        return cause;
    }

    /**
     * Method getName.
     *
     * @return return value.
     */
    public String getName() {
        return name;
    }

    /**
     * Method getDuration.
     *
     * @return return value.
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * Method getStartTime.
     *
     * @return return value.
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Method getEndTime.
     *
     * @return return value.
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Method prePostFreeMemory.
     *
     * @return return value.
     */
    @JsonAnyGetter
    public Map<String, Long> prePostFreeMemory() {
        return prePostFreeMemory;
    }

    /**
     * Class Builder.
     */
    public static class Builder {

        private String action;
        private String cause;
        private String name;
        private Long duration;
        private Long startTime;
        private Long endTime;

        private Map<String, Long> prePostGcFreeMemory = new HashMap<>();

        /**
         * Method action.
         *
         * @param action action.
         * @return return value.
         */
        public Builder action(final String action) {
            this.action = action;
            return this;
        }

        /**
         * Method cause.
         *
         * @param cause cause.
         * @return return value.
         */
        public Builder cause(final String cause) {
            this.cause = cause;
            return this;
        }

        /**
         * Method name.
         *
         * @param name name.
         * @return return value.
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Method durationMillis.
         *
         * @param duration duration.
         * @return return value.
         */
        public Builder durationMillis(final Long duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Method startTime.
         *
         * @param startTime startTime.
         * @return return value.
         */
        public Builder startTime(final Long startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * Method endTime.
         *
         * @param endTime endTime.
         * @return return value.
         */
        public Builder endTime(final Long endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * Size pre collection of a particular mem pool.
         * @param memPool Name of memory pool.
         * @param preGc Size in bytes pre collection.
         * @return This builder.
         */
        public Builder preGcFreeMem(final String memPool, final Long preGc) {
            this.prePostGcFreeMemory.put(formatPreName(memPool), preGc);
            return this;
        }

        /**
         * Size post collection of a particular mem pool.
         * @param memPool Name of memory pool.
         * @param postGc Size in bytes post collection.
         * @return This builder.
         */
        public Builder postGcFreeMem(final String memPool, final Long postGc) {
            this.prePostGcFreeMemory.put(formatPostName(memPool), postGc);
            return this;
        }

        /**
         * Build a new GcEventData object based on the currently configured builder.
         * @return Immutable GcEventData.
         */
        public GcEventData build() {
            return new GcEventData(action, cause, name, duration, startTime, endTime, prePostGcFreeMemory);
        }

        private static String formatPreName(final String memPool) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, memPool.replace(" ", "_").toLowerCase()) + "PreGc";
        }

        private static String formatPostName(final String memPool) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, memPool.replace(" ", "_").toLowerCase()) + "PostGc";
        }

    }

}
