package com.blispay.common.metrics.metric;

import java.util.Collections;
import java.util.Map;

public abstract class MetricContext {

    public abstract Map<String, String> getContextMap();

    public Map<String, String> readOnlyContext() {
        return Collections.unmodifiableMap(getContextMap());
    }

}
