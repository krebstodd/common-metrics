package com.blispay.common.metrics.event;

import com.blispay.common.metrics.metric.Measurement;
import com.blispay.common.metrics.metric.MetricType;
import com.blispay.common.metrics.metric.MetricContext;
import com.blispay.common.metrics.metric.MetricName;
import com.blispay.common.metrics.metric.MetricClass;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MetricEvent {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    private static final String timestampKey = "timestamp";
    private static final String nameKey = "name";
    private static final String levelKey = "level";
    private static final String classKey = "class";
    private static final String typeKey = "type";

    public final ZonedDateTime timestamp;

    public final MetricName mName;
    public final MetricType mType;
    public final MetricClass mClass;

    public final Level level;

    public final Measurement measurement;
    public final MetricContext mContext;

    public MetricEvent(final MetricName mName,
                       final MetricType mType,
                       final MetricClass mClass,
                       final Measurement measurement,
                       final MetricContext mContext,
                       final ZonedDateTime timestamp) {

        this(mName, mType, mClass, measurement, mContext, timestamp, Level.INFO);
    }

    public MetricEvent(final MetricName mName,
                       final MetricType mType,
                       final MetricClass mClass,
                       final Measurement measurement,
                       final MetricContext mContext,
                       final ZonedDateTime timestamp,
                       final Level level) {

        this.mName = mName;
        this.mClass = mClass;
        this.mType = mType;
        this.measurement = measurement;
        this.mContext = mContext;
        this.timestamp = timestamp;
        this.level = level;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public MetricName getName() {
        return mName;
    }

    public MetricClass getMetricClass() {
        return mClass;
    }

    public MetricType getType() {
        return mType;
    }

    public Level getLevel() {
        return level;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public MetricContext getContext() {
        return mContext;
    }

    public String printJson() {

        final JSONObject json = new JSONObject();
        json.put(timestampKey, TIMESTAMP_FORMATTER.format(timestamp))
                .put(nameKey, mName.getValue())
                .put(classKey, mClass.getValue())
                .put(typeKey, mType.getValue())
                .put(levelKey, level.name())
                .put(measurement.getValueKey(), measurement.getValue().toString())
                .put(measurement.getUnitsKey(), measurement.getUnits());

        mContext.readOnlyContext().forEach(json::put);

        return json.toString();

    }

    @Override
    public String toString() {
        return printJson();
    }

    private static String keyVal(final String key, final String val) {
        return key + "=[" + val + "]";
    }

    public static enum Level {
        INFO,
        WARNING,
        ERROR
    }

}
