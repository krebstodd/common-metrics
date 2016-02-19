package com.blispay.common.metrics;

import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.MetricName;
import com.blispay.common.metrics.model.MetricClass;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

public class MetricEventMatcher extends TypeSafeMatcher<MetricEvent> {

    private final Matcher<String> name;
    private final Matcher<String> type;
    private final Matcher<String> mClass;
    private final Map<String, String> context;
    private final MeasurementMatcher mMeasurement;

    public MetricEventMatcher(final MetricName name, final MetricType mType, final MetricClass mClass, final Map<String, String> mContext) {
        this(name, mType, mClass, mContext, new MeasurementMatcher.NotEmpty());
    }

    public MetricEventMatcher(final MetricName name, final MetricType mType, final MetricClass mClass,
                              final Map<String, String> mContext, final MeasurementMatcher mMeasurement) {

        this(name.getValue(), mType.getValue(), mClass.getValue(), mContext, mMeasurement);
    }

    public MetricEventMatcher(final String name, final String mType, final String mClass,
                              final Map<String, String> mContext, final MeasurementMatcher mMeasurement) {

        this.name = Matchers.equalTo(name);
        this.type = Matchers.equalTo(mType);
        this.mClass = Matchers.equalTo(mClass);
        this.context = mContext;
        this.mMeasurement = mMeasurement;

    }

    @Override
    public boolean matchesSafely(final MetricEvent evt) {
        System.out.println(">> " + evt.getName().toString());
        System.out.println(name.matches(evt.getName().getValue()));
        System.out.println(type.matches(evt.getType().getValue()));
        System.out.println(mClass.matches(evt.getMetricClass().getValue()));
        System.out.println(mMeasurement.matches(evt.getMeasurement()));
        System.out.println(context.entrySet().stream().allMatch(entry -> evt.getContext().readOnlyContext().get(entry.getKey()).equals(entry.getValue())));
        System.out.println(">>");

        return name.matches(evt.getName().getValue())
                && type.matches(evt.getType().getValue())
                && mClass.matches(evt.getMetricClass().getValue())
                && mMeasurement.matches(evt.getMeasurement())
                && context.entrySet().stream().allMatch(entry -> evt.getContext().readOnlyContext().get(entry.getKey()).equals(entry.getValue()));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("name=[")
                .appendDescriptionOf(name)
                .appendText("], type=[")
                .appendDescriptionOf(type)
                .appendText("], class=[")
                .appendDescriptionOf(mClass)
                .appendText("], context=[")
                .appendValue(context)
                .appendText("], measurement=[")
                .appendValue(mMeasurement)
                .appendText("]");
    }

}
