package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.Measurement;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class MeasurementMatcher extends TypeSafeMatcher<Measurement> {

    private final Matcher<String> valKeyMatcher;
    private final Matcher<String> valMatcher;
    private final Matcher<String> unitKeyMatcher;
    private final Matcher<String> unitMatcher;
    
    public MeasurementMatcher(final String valKey, final String val, final String unitKey, final String unit) {
        this.valKeyMatcher = Matchers.equalTo(valKey);
        this.valMatcher = Matchers.equalTo(val);
        this.unitKeyMatcher = Matchers.equalTo(unitKey);
        this.unitMatcher = Matchers.equalTo(unit);
    }

    public MeasurementMatcher(final Matcher<String> valKey, final Matcher<String> val, final Matcher<String> unitKey, final Matcher<String> unit) {
        this.valKeyMatcher = valKey;
        this.valMatcher = val;
        this.unitKeyMatcher = unitKey;
        this.unitMatcher = unit;
    }



    @Override
    public boolean matchesSafely(final Measurement measurement) {

        System.out.println("MEASUREMENT =================" + measurement.getValue());
        System.out.println(valKeyMatcher.matches(measurement.getValueKey()));
                System.out.println(valMatcher.matches(measurement.getValue().toString()));
                System.out.println(unitKeyMatcher.matches(measurement.getUnitsKey()));
                System.out.println(unitMatcher.matches(measurement.getUnits()));
        System.out.println("END MEASUREMENT =================");

        return valKeyMatcher.matches(measurement.getValueKey())
                && valMatcher.matches(measurement.getValue().toString())
                && unitKeyMatcher.matches(measurement.getUnitsKey())
                && unitMatcher.matches(measurement.getUnits());
    }

    @Override
    public void describeTo(final Description description) {

    }

    public static class NotEmpty extends MeasurementMatcher {

        public NotEmpty() {
            super(Matchers.notNullValue(String.class), Matchers.notNullValue(String.class), Matchers.notNullValue(String.class), Matchers.notNullValue(String.class));
        }
    }
}
