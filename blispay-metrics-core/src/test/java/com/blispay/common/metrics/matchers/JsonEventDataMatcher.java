package com.blispay.common.metrics.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONObject;

import java.util.Map;

/**
 * Class JsonEventDataMatcher.
 */
public class JsonEventDataMatcher extends TypeSafeMatcher<JSONObject> {

    private final Map<String, Object> expected;

    /**
     * Constructs JsonEventDataMatcher.
     *
     * @param expectedData expectedData.
     */
    public JsonEventDataMatcher(final Map<String, Object> expectedData) {
        this.expected = expectedData;
    }

    @Override
    public boolean matchesSafely(final JSONObject jsonObject) {
        return expected.entrySet().stream().allMatch(entry -> jsonObject.get(entry.getKey()).equals(entry.getValue()));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(expected);
    }

}
