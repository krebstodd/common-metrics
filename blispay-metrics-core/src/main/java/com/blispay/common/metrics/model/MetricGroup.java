package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetricGroup {

    ACCOUNT_DOMAIN("metrics.business.account-domain"),
    ACTIVITY_DOMAIN("metrics.business.activity-domain"),
    API_DOMAIN("metrics.business.api-domain"),
    MERCHANT_DOMAIN("metrics.business.merchant-domain"),
    NOTIFICATION_DOMAIN("metrics.business.notification-domain"),
    PAYMENT_DOMAIN("metrics.business.payment-domain"),
    SOR_DOMAIN("metrics.business.sor-domain"),
    STATEMENT_DOMAIN("metrics.business.statement-domain"),
    TRANSACTION_DOMAIN("metrics.business.transaction-domain"),
    USER_DOMAIN("metrics.business.user-domain"),

    SERVER("metrics.server"),
    SERVER_HTTP("metrics.server.http"),
    SERVER_MESSAGE_QUEUE("metrics.server.mq"),

    CLIENT("metrics.client"),

    CLIENT_HTTP("metrics.client.http"),
    CLIENT_HTTP_KMS("metrics.client.http.kms"),
    CLIENT_HTTP_TWILIO("metrics.client.http.twilio"),
    CLIENT_HTTP_SMARTY_STREETS("metrics.client.http.smarty-streets"),
    CLIENT_HTTP_SENDGRID("metrics.client.http.sendgrid"),
    CLIENT_HTTP_WHITE_PAGES_PRO("metrics.client.http.white-pages-pro"),

    CLIENT_JDBC("metrics.client.jdbc"),
    CLIENT_REDIS("metrics.client.redis"),

    CLIENT_MESSAGE_QUEUE("metrics.client.mq"),

    CLIENT_MESSAGE_BUS("metrics.client.bus"),
    CLIENT_MESSAGE_BUS_COMMAND("metrics.client.bus.command"),
    CLIENT_MESSAGE_BUS_EVENT("metrics.client.bus.event"),

    RESOURCE_UTILIZATION("metrics.resource"),
    RESOURCE_UTILIZATION_GC("metrics.resource.gc"),
    RESOURCE_UTILIZATION_MEM("metrics.resource.memory"),
    RESOURCE_UTILIZATION_THREADS("metrics.resource.threads"),
    RESOURCE_UTILIZATION_LOGGING("metrics.resource.logging"),
    RESOURCE_UTILIZATION_CACHE("metrics.resource.cache"),

    INTERNAL_METHOD_CALL("metrics.internal.method");

    private final String groupName;

    private MetricGroup(final String groupName) {
        this.groupName = groupName;
    }

    @JsonValue
    public String getValue() {
        return groupName;
    }
}
