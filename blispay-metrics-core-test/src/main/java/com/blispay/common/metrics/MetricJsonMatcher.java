package com.blispay.common.metrics;

import com.blispay.common.metrics.model.Measurement;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.MetricName;
import com.blispay.common.metrics.model.MetricClass;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONObject;

import java.util.Map;

public class MetricJsonMatcher extends TypeSafeMatcher<String> {

    private final Matcher<String> name;
    private final Matcher<String> type;
    private final Matcher<String> mClass;
    private final Map<String, String> context;
    private final Measurement mMeasurement;

    public MetricJsonMatcher(final MetricName mName, final MetricClass mClass, final MetricType mType,
                             final Map<String, String> mContext, final Measurement mMeasurement) {

        this.name = Matchers.equalTo(mName.getValue());
        this.type = Matchers.equalTo(mType.getValue());
        this.mClass = Matchers.equalTo(mClass.getValue());
        this.context = mContext;
        this.mMeasurement = mMeasurement;

    }

    @Override
    public boolean matchesSafely(final String json) {
        final JSONObject jsonObj = new JSONObject(json);

        return name.matches(jsonObj.getString("name"))
                && type.matches(jsonObj.getString("type"))
                && mClass.matches(jsonObj.getString("class"))
                && context.entrySet().stream().allMatch(entry -> jsonObj.getString(entry.getKey()).equals(entry.getValue()))
                && mMeasurement.getValue().equals(jsonObj.getString(mMeasurement.getValueKey()))
                && mMeasurement.getUnits().equals(jsonObj.getString(mMeasurement.getUnitsKey()));
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
