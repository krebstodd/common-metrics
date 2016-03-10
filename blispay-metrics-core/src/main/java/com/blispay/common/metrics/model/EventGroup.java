package com.blispay.common.metrics.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventGroup {

    // CHECK_OFF: JavadocVariable

    ACCOUNT_DOMAIN("metrics.business.account-domain"),
    ACCOUNT_DOMAIN_HEALTH("metrics.health.account-domain"),

    ACTIVITY_DOMAIN("metrics.business.activity-domain"),
    ACTIVITY_DOMAIN_HEALTH("metrics.health.activity-domain"),

    API_DOMAIN("metrics.business.api-domain"),
    API_DOMAIN_HEALTH("metrics.health.api-domain"),

    MERCHANT_DOMAIN("metrics.business.merchant-domain"),
    MERCHANT_DOMAIN_HEALTH("metrics.health.merchant-domain"),

    NOTIFICATION_DOMAIN("metrics.business.notification-domain"),
    NOTIFICATION_DOMAIN_HEALTH("metrics.health.notification-domain"),

    PAYMENT_DOMAIN("metrics.business.payment-domain"),
    PAYMENT_DOMAIN_HEALTH("metrics.health.payment-domain"),

    SOR_DOMAIN("metrics.business.sor-domain"),
    SOR_DOMAIN_HEALTH("metrics.health.sor-domain"),

    STATEMENT_DOMAIN("metrics.business.statement-domain"),
    STATEMENT_DOMAIN_HEALTH("metrics.health.statement-domain"),

    TRANSACTION_DOMAIN("metrics.business.transaction-domain"),
    TRANSACTION_DOMAIN_HEALTH("metrics.health.transaction-domain"),

    USER_DOMAIN("metrics.business.user-domain"),
    USER_DOMAIN_HEALTH("metrics.health.user-domain"),

    SERVER_HTTP("metrics.server.http"),
    SERVER_MESSAGE_QUEUE("metrics.server.mq"),

    CLIENT_HTTP_KMS("metrics.client.http.kms"),
    CLIENT_HTTP_TWILIO("metrics.client.http.twilio"),
    CLIENT_HTTP_SMARTY_STREETS("metrics.client.http.smarty-streets"),
    CLIENT_HTTP_SENDGRID("metrics.client.http.sendgrid"),
    CLIENT_HTTP_WHITE_PAGES_PRO("metrics.client.http.white-pages-pro"),
    CLIENT_HTTP_DDS("metrics.client.http.dds"),
    CLIENT_HTTP_QR("metrics.client.http.quick-remit"),

    CLIENT_JDBC("metrics.client.jdbc"),
    CLIENT_REDIS("metrics.client.redis"),

    CLIENT_MQ_REQ("metrics.client.mq.req"),
    CLIENT_MQ_EVT("metrics.client.mq.evt"),

    CLIENT_MESSAGE_BUS_COMMAND("metrics.client.bus.command"),
    CLIENT_MESSAGE_BUS_EVENT("metrics.client.bus.event"),

    RESOURCE_UTILIZATION_GC("metrics.resource.gc"),
    RESOURCE_UTILIZATION_MEM("metrics.resource.memory"),
    RESOURCE_UTILIZATION_THREADS("metrics.resource.threads"),
    RESOURCE_UTILIZATION_LOGGING("metrics.resource.logging"),
    RESOURCE_UTILIZATION_CACHE("metrics.resource.cache"),

    INTERNAL_METHOD_CALL("metrics.internal.method"),
    DATA_SERIALIZATION("metrics.internal.serialization"),

    HEALTH("metrics.health");

    // CHECK_ON: JavadocVariable

    private final String groupName;

    private EventGroup(final String groupName) {
        this.groupName = groupName;
    }

    @JsonValue
    public String getValue() {
        return groupName;
    }
}
