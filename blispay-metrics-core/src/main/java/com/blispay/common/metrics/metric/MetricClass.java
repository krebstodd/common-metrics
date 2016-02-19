package com.blispay.common.metrics.metric;

public class MetricClass {

    private final String typeName;

    public MetricClass(final String typeName) {
        this.typeName = typeName;
    }


    public String getValue() {
        return this.typeName;
    }

    public static MetricClass httpRequest() {
        return new MetricClass("HTTP");
    }

    public static MetricClass jdbcTransaction() {
        return new MetricClass("JDBC");
    }

    public static MetricClass apiCall() {
        return new MetricClass("API");
    }

    public static MetricClass businessEvent() {
        return new MetricClass("BE");
    }

    public static MetricClass executionTime() {
        return new MetricClass("EXEC");
    }

    public static MetricClass threadPool() {
        return new MetricClass("TP");
    }

}
