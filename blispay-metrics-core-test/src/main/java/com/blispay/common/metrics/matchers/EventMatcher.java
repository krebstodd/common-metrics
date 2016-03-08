package com.blispay.common.metrics.matchers;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class EventMatcher<T> extends TypeSafeMatcher<EventModel<T>> {

    private final Matcher<String> timestampMatcher;
    private final Matcher<String> applicationMatcher;
    private final Matcher<String> groupMatcher;
    private final Matcher<String> nameMatcher;
    private final Matcher<String> typeMatcher;
    private final Matcher<T> dataMatcher;

    /**
     * Resource call matcher.
     * @param applicationId application.
     * @param group group
     * @param name name
     * @param metricType type
     * @param eventDataMatcher data matcher
     */
    public EventMatcher(final String applicationId,
                        final EventGroup group, final String name,
                        final EventType metricType, final Matcher<T> eventDataMatcher) {

        this.applicationMatcher = Matchers.equalTo(applicationId);
        this.timestampMatcher = Matchers.endsWith("Z");
        this.groupMatcher = Matchers.equalTo(group.getValue());
        this.nameMatcher = Matchers.equalTo(name);
        this.typeMatcher = Matchers.equalTo(metricType.getValue());

        this.dataMatcher = eventDataMatcher;

    }

    @Override
    public boolean matchesSafely(final EventModel<T> metric) {
        return timestampMatcher.matches(metric.getTimestamp())
                && applicationMatcher.matches(metric.getApplication())
                && groupMatcher.matches(metric.getGroup().getValue())
                && nameMatcher.matches(metric.getName())
                && typeMatcher.matches(metric.getType().getValue())
                && dataMatcher.matches(metric.eventData());
    }

    @Override
    public void describeTo(final Description description) {

    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String application;
        private EventGroup group;
        private String name;
        private EventType type;
        private Matcher<Object> dataMatcher;

        public Builder setApplication(final String application) {
            this.application = application;
            return this;
        }

        public Builder setGroup(final EventGroup group) {
            this.group = group;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setType(final EventType type) {
            this.type = type;
            return this;
        }

        public Builder setDataMatcher(final Matcher<Object> dataMatcher) {
            this.dataMatcher = dataMatcher;
            return this;
        }

        public <T> EventMatcher<T> build() {
            return new EventMatcher<>(application, group, name, type, (Matcher<T>) dataMatcher);
        }
    }
}

