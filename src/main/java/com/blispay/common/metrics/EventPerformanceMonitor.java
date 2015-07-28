package com.blispay.common.metrics;

import static com.codahale.metrics.MetricRegistry.name;

//CHECK_OFF: FinalClass
public class EventPerformanceMonitor extends ApplicationMonitor {

    private final String namespace;

    private EventPerformanceMonitor(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Start a new timer in the provided namespace. If one doesn't exist, create it and use it.
     *
     * @param names List of namespaces for the metric inside of hte category namespace.
     * @return MetricResolver to be called when the metric has concluded.
     */
    public MetricResolver start(final String... names) {
        return registry
                .timer(name(namespace, extendVarArgs(names, RESPONSE_TIME)))
                .time()
                ::stop;
    }

    public static EventPerformanceMonitor getMonitor(final String namespace) {
        return new EventPerformanceMonitor(namespace);
    }

}
//CHECK_ON: FinalClass

