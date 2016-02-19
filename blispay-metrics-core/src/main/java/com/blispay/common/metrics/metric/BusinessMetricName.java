package com.blispay.common.metrics.metric;

import java.util.Arrays;
import java.util.List;

public class BusinessMetricName extends MetricName {

    private final String domain;

    private final String action;

    public BusinessMetricName(final String domain, final String action) {
        this.domain = domain;
        this.action = action;
    }

    public String getDomain() {
        return domain;
    }

    public String getAction() {
        return action;
    }

    @Override
    public List<String> nameComponents() {
        return Arrays.asList(domain, action);
    }
}
