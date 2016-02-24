package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class DataSourceResourceCallMetric extends BaseResourceCallMetric<DataSourceResourceCallEventData> {

    private final DataSourceResourceCallEventData eventData;

    /**
     * Immutable data source query metric.
     *
     * @param timestamp timestamp for when the metric occurred.
     * @param applicationId application name.
     * @param group metric group.
     * @param name metric name.
     * @param eventData summary of the query and execution time.
     */
    public DataSourceResourceCallMetric(final ZonedDateTime timestamp,
                                        final String applicationId,
                                        final MetricGroup group,
                                        final String name,
                                        final DataSourceResourceCallEventData eventData) {

        super(timestamp, applicationId, group, name);

        this.eventData = eventData;
    }

    @Override
    public DataSourceResourceCallEventData eventData() {
        return this.eventData;
    }

}
