package com.blispay.common.metrics;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class HibernateProbe extends BpMetricProbe {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateProbe.class);

    private final SessionFactory sessionFactory;

    private final String id;

    private final Boolean collectQueryStats;

    private final ConcurrentHashMap<String, BpGauge> queryGauges = new ConcurrentHashMap<>();

    /**
     * Create a new hibernate probe for the provided entity manager factory.
     *
     * @param entityManager Entity manager factory containing the hibernate session factory we want to probe.
     * @param id An identifier for the specific entity manager factory as apps may have multiple entity managers they want to probe.
     * @param collectQueryStats Collect statistics on a per-query basis.
     */
    public HibernateProbe(final HibernateEntityManagerFactory entityManager, final String id, final Boolean collectQueryStats) {

        this.sessionFactory = entityManager.getSessionFactory();
        this.sessionFactory.getStatistics().setStatisticsEnabled(true);
        this.id = id;
        this.collectQueryStats = collectQueryStats;

        if (this.collectQueryStats) {
            replaceStatisticsServiceBinding();
        }

        init();
    }

    private void init() {
        metricService.createGauge(HibernateProbe.class, name("statistics"), "Hibernate statistics",
                sessionFactory::getStatistics);
    }

    private String name(final String metric) {
        return id + "." + metric;
    }

    private void replaceStatisticsServiceBinding() {
        final SessionFactoryImpl impl = (SessionFactoryImpl) sessionFactory;
        final ServiceRegistryImplementor reg = impl.getServiceRegistry();
        final ServiceBinding<StatisticsImplementor> binding = reg.locateServiceBinding(StatisticsImplementor.class);
        binding.setService(new NewQueryStatisticsInterceptor());
    }

    private BpGauge addQueryGauge(final String query) {
        return metricService.createGauge(HibernateProbe.class, query, "Hibernate statistics related to a masked query string.",
                () -> this.getQueryStats(query));
    }

    private QueryStatistics getQueryStats(final String query) {
        return sessionFactory.getStatistics().getQueryStatistics(query);
    }

    @Override
    protected void startProbe() {}

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    private class NewQueryStatisticsInterceptor extends ConcurrentStatisticsImpl {

        @Override
        public QueryStatistics getQueryStatistics(final String hqlString) {
            System.out.println("ADDING NEW QUERY: " + queryGauges.containsKey(hqlString));
            queryGauges.computeIfAbsent(hqlString, HibernateProbe.this::addQueryGauge);
            return super.getQueryStatistics(hqlString);
        }

    }

}
