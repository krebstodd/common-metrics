package com.blispay.common.metrics;

import com.blispay.common.metrics.model.MappedMetricContext;
import com.blispay.common.metrics.model.MetricContext;

import java.util.HashMap;
import java.util.Map;

public final class HttpRequestMetricContextFactory {

    private static final String methodKey = "method";
    private static final String statusCodeKey = "statusCode";
    private static final String pathKey = "path";

    private HttpRequestMetricContextFactory() {}

    public static MetricContext createContext(final String method, final Integer statusCode, final String path) {
        final Map<String, String> contextMap = new HashMap<>();
        contextMap.put(methodKey, method);
        contextMap.put(statusCodeKey, String.valueOf(statusCode));
        contextMap.put(pathKey, path);
        return new MappedMetricContext(contextMap);
    }

}
