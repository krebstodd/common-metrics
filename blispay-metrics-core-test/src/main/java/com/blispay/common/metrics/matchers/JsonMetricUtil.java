package com.blispay.common.metrics.matchers;

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
        return parseValue(obj, "trackingInfo", JSONObject.class);
    }

    public static JSONObject parseEventData(final JSONObject obj) {
        return parseValue(obj, "eventData", JSONObject.class);
    }

    public static <T> T parseValue(final JSONObject obj, final String key, final Class<T> type) {

        if (key.contains(".")) {

            final String[] keyPath = key.split(".");
            final String newKey = String.join(".", Arrays.copyOfRange(keyPath, 1, keyPath.length));

            return parseValue(obj.getJSONObject(keyPath[0]), newKey, type);

        } else {
            return (T) obj.get(key);
        }

    }
    
}
