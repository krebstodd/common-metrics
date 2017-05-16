package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.TrackingInfo;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

/**
 * Class EventMatcher.
 *
 * @param <D> Generic param type.
 * @param <U> Generic param type.
 */
public class EventMatcher<D, U> extends TypeSafeMatcher<EventModel<D, U>> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> applicationMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<TrackingInfo> trackingInfoMatcher;
    private final Matcher<D> dataMatcher;
    private final Matcher<U> userDataMatcher;

    /**
     * Resource call matcher.
     * @param applicationId application.
     * @param group group
     * @param name name
     * @param metricType type
     * @param dataMatcher data
     * @param userDataMatcher user data
     * @param trackingInfo tracking info
     */
    public EventMatcher(final String applicationId, final EventGroup group, final String name, final EventType metricType, final Matcher<D> dataMatcher, final Matcher<U> userDataMatcher,
                        final Matcher<TrackingInfo> trackingInfo) {

        this.applicationMatcher = Matchers.equalTo(applicationId);
        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(metricType.getValue());

        this.trackingInfoMatcher = trackingInfo;
        this.dataMatcher = dataMatcher;
        this.userDataMatcher = userDataMatcher;

    }

    @Override
    public boolean matchesSafely(final EventModel<D, U> metric) {
        return timestampMatcher.matches(metric.getHeader().getTimestamp())
               && applicationMatcher.matches(metric.getHeader().getApplication())
               && groupMatcher.matches(metric.getHeader().getGroup().getValue())
               && nameMatcher.matches(metric.getHeader().getName())
               && typeMatcher.matches(metric.getHeader().getType().getValue())
               && dataMatcher.matches(metric.getData())
               && userDataMatcher.matches(metric.getUserData())
               && trackingInfoMatcher.matches(metric.getHeader().getTrackingInfo());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("EventModel(timestamp=[")
                   .appendDescriptionOf(timestampMatcher)
                   .appendText("], application=[")
                   .appendDescriptionOf(applicationMatcher)
                   .appendText("], group=[")
                   .appendDescriptionOf(groupMatcher)
                   .appendText("], name=[")
                   .appendDescriptionOf(nameMatcher)
                   .appendText("], type=[")
                   .appendDescriptionOf(typeMatcher)
                   .appendText("], data=[")
                   .appendDescriptionOf(dataMatcher)
                   .appendText("], userData=[")
                   .appendDescriptionOf(userDataMatcher)
                   .appendText("], trackingInfo=[")
                   .appendDescriptionOf(trackingInfoMatcher)
                   .appendText("])");
    }

    @Override
    protected void describeMismatchSafely(final EventModel<D, U> item, final Description mismatchDescription) {
        mismatchDescription.appendText("was EventModel(timestamp=[")
                           .appendValue(item.getHeader().getTimestamp())
                           .appendText("], application=[")
                           .appendValue(item.getHeader().getApplication())
                           .appendText("], group=[")
                           .appendValue(item.getHeader().getGroup().getValue())
                           .appendText("], name=[")
                           .appendValue(item.getHeader().getName())
                           .appendText("], type=[")
                           .appendValue(item.getHeader().getType())
                           .appendText("], data=[")
                           .appendValue(item.getData())
                           .appendText("], userData=[")
                           .appendValue(Optional.ofNullable(item.getUserData()).map(ReflectionToStringBuilder::toString).orElse(""))
                           .appendText("], trackingInfo=[")
                            .appendValue(Optional.ofNullable(item.getHeader().getTrackingInfo()).map(ReflectionToStringBuilder::toString).orElse(""))
                           .appendText("])");
    }

    /**
     * Method builder.
     *
     * @param <D> Data matcher.
     * @param <U> User data matcher.
     * @return return value.
     */
    public static <D, U> Builder<D, U> builder() {
        return new Builder<>();
    }

    /**
     * Class Builder.
     *
     * @param <D> Generic param type.
     * @param <U> Generic param type.
     */
    public static class Builder<D, U> {

        private String application;
        private EventGroup group;
        private String name;
        private EventType type;
        private Matcher<TrackingInfo> trackingInfo;
        private Matcher<D> dataMatcher;
        private Matcher<U> userDataMatcher;

        /**
         * Method setApplication.
         *
         * @param application application.
         * @return return value.
         */
        public Builder<D, U> setApplication(final String application) {
            this.application = application;
            return this;
        }

        /**
         * Method setGroup.
         *
         * @param group group.
         * @return return value.
         */
        public Builder<D, U> setGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        /**
         * Method setName.
         *
         * @param name name.
         * @return return value.
         */
        public Builder<D, U> setName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Method setType.
         *
         * @param type type.
         * @return return value.
         */
        public Builder<D, U> setType(final EventType type) {
            this.type = type;
            return this;
        }

        /**
         * Method setTrackingInfoMatcher.
         *
         * @param trackingInfo trackingInfo.
         * @return return value.
         */
        public Builder<D, U> setTrackingInfoMatcher(final Matcher<TrackingInfo> trackingInfo) {
            this.trackingInfo = trackingInfo;
            return this;
        }

        /**
         * Method setDataMatcher.
         *
         * @param dataMatcher dataMatcher.
         * @return return value.
         */
        public Builder<D, U> setDataMatcher(final Matcher<D> dataMatcher) {
            this.dataMatcher = dataMatcher;
            return this;
        }

        /**
         * Method setUserDataMatcher.
         *
         * @param userDataMatcher userDataMatcher.
         * @return return value.
         */
        public Builder<D, U> setUserDataMatcher(final Matcher<U> userDataMatcher) {
            this.userDataMatcher = userDataMatcher;
            return this;
        }

        /**
         * Build a new event matcher instance.
         * @return new event matcher.
         */
        public EventMatcher<D, U> build() {

            // CHECK_OFF: AvoidInlineConditionals
            final Matcher user = userDataMatcher == null
                                 ? Matchers.nullValue()
                                 : userDataMatcher;
            final Matcher data = dataMatcher == null
                                 ? Matchers.nullValue()
                                 : dataMatcher;
            final Matcher trackingInfoMatcher = trackingInfo == null
                                                ? Matchers.nullValue()
                                                : trackingInfo;

            // CHECK_ON: AvoidInlineConditionals

            return new EventMatcher<>(application, group, name, type, data, user, trackingInfoMatcher);

        }

    }

}
