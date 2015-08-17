package com.blispay.common.metrics.probe;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateProbe extends BpMetricProbe {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateProbe.class);

    private final SessionFactory sessionFactory;

    private final String id;

    public HibernateProbe(final HibernateEntityManagerFactory entityManager, final String id) {
        this.sessionFactory = entityManager.getSessionFactory();
        this.id = id;
        init();
    }

    private void init() {
        metricService.createGauge(HibernateProbe.class, name("statistics"), "Hibernate statistics",
                sessionFactory::getStatistics);
    }

    private String name(final String metric) {
        return id + "." + metric;
    }

    @Override
    protected void startProbe() {}

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
