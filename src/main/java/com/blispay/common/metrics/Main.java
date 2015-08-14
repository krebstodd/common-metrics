package com.blispay.common.metrics;

public class Main {

    public static void main(final String[] args) throws InterruptedException {

        System.setProperty("metrics.jvm.enabled", "true");
        System.setProperty("metrics.jmx.enabled", "true");

        BpMetricService.getInstance();
        Thread.sleep(600000);

    }
}
