package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.BaseMetricFactory;
import com.blispay.common.metrics.model.MetricGroup;

public class DataSourceResourceCallMetricFactory extends BaseMetricFactory<DataSourceResourceCallMetric, DataSourceResourceCallEventData> {

    public DataSourceResourceCallMetricFactory(final String application, final MetricGroup group, final String name) {
        super(application, group, name);
    }

    public DataSourceResourceCallMetric newMetric(final DataSourceResourceCallEventData eventData) {
        return new DataSourceResourceCallMetric(timestamp(), getApplication(), getGroup(), getName(), eventData);
    }

}
