package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.call.BaseResourceCallMetric;

import java.time.ZonedDateTime;

public class DataSourceResourceCallMetric extends BaseResourceCallMetric<DataSourceResourceCallEventData> {

    private final DataSourceResourceCallEventData eventData;

    public DataSourceResourceCallMetric(final ZonedDateTime timestamp,
                                        final MetricGroup group,
                                        final String name,
                                        final DataSourceResourceCallEventData eventData) {

        super(timestamp, group, name);

        this.eventData = eventData;
    }

    @Override
    public DataSourceResourceCallEventData eventData() {
        return this.eventData;
    }

}
