package com.blispay.common.metrics.matchers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public final class JsonMetricUtil {

    private JsonMetricUtil() {

    }

    public static String parseTimeStamp(final JSONObject obj) {
        return parseValue(obj, "timestamp", String.class);
    }

    public static String parseApplication(final JSONObject obj) {
        return parseValue(obj, "application", String.class);
    }

    public static String parseGroup(final JSONObject obj) {
        return parseValue(obj, "group", String.class);
    }

    public static String parseName(final JSONObject obj) {
        return parseValue(obj, "name", String.class);
    }

    public static String parseType(final JSONObject obj) {
        return parseValue(obj, "type", String.class);
    }

    public static JSONObject parseTrackingInfo(final JSONObject obj) {
        return parseValue(obj, "createAndSetThreadLocalTrackingInfo", JSONObject.class);
    }

    /**
     * Parse data element.
     * @param obj json object
     * @return data element object.
     */
    public static JSONObject parseData(final JSONObject obj) {

        try {
            return parseValue(obj, "data", JSONObject.class);
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Parse user data element.
     * @param obj json object
     * @return user data element object.
     */
    public static JSONObject parseUserData(final JSONObject obj) {
        try {
            return parseValue(obj, "userData", JSONObject.class);
        } catch (JSONException ex) {
            return null;
        }
    }

    private static <T> T parseValue(final JSONObject obj, final String key, final Class<T> type) {

        if (key.contains(".")) {

            final String[] keyPath = key.split(".");
            final String newKey = String.join(".", Arrays.copyOfRange(keyPath, 1, keyPath.length));

            return parseValue(obj.getJSONObject(keyPath[0]), newKey, type);

        } else {
            return (T) obj.get(key);
        }

    }
    
}
