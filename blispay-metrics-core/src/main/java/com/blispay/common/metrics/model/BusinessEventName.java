package com.blispay.common.metrics.model;

public class BusinessEventName extends MetricName {

    public BusinessEventName(final Domain domain, final String action) {
        addComponent(domain.getValue());
        addComponent(action);
    }

    public BusinessEventName(final Domain domain, final String subDomain, final String action) {
        addComponent(domain.getValue());
        addComponent(subDomain);
        addComponent(action);
    }

    public static enum Domain {

        USER("user"),
        ACCOUNT("account"),
        PAYMENT("payment"),
        STATEMENT("statement"),
        NOTIFICATION("notification"),
        ACTIVITY("activity");

        private final String domainName;

        private Domain(final String domainName) {
            this.domainName = domainName;
        }

        public String getValue() {
            return domainName;
        }

    }
}
