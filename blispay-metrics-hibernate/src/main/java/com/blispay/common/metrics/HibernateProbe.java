package com.blispay.common.metrics;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateProbe extends BpMetricProbe {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateProbe.class);

    private final SessionFactory sessionFactory;

    private final String id;

    /**
     * Create a new hibernate probe for the provided entity manager factory.
     *
     * @param entityManager Entity manager factory containing the hibernate session factory we want to probe.
     * @param id An identifier for the specific entity manager factory as apps may have multiple entity managers.
     */
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
