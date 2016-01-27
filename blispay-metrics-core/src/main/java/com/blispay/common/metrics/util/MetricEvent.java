package com.blispay.common.metrics.util;

public class MetricEvent<T> {

    private final String eventKey;

    private final String metricName;

    private final Class<?> owner;

    private final T value;

    /**
     * Create a new recordable event containing the events warning level and the sample at time of the event.
     *
     * @param owner The event owning class.
     * @param metricName Metric name.
     * @param eventKey Event description.
     * @param eventValue The value of the event.
     */
    public MetricEvent(final Class<?> owner, final String metricName, final String eventKey, final T eventValue) {
        this.owner = owner;
        this.metricName = metricName;
        this.eventKey = eventKey;
        this.value = eventValue;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getMetricName() {
        return metricName;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public T getValue() {
        return value;
    }

    /**
     * Build a printable string representing the event properties.
     * @return String representation of the event.
     */
    public String print() {
        return new StringBuilder()
                .append("owner=[")
                .append(owner.getName())
                .append("],name=[")
                .append(metricName)
                .append("],eventKey=[")
                .append(eventKey)
                .append("],value=[")
                .append(value)
                .append("]").toString();
    }

}
