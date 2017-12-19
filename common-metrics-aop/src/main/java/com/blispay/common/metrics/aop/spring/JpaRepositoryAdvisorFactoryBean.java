package com.blispay.common.metrics.aop.spring;

import com.blispay.common.metrics.MetricService;

/**
 * Spring advisor factory producing advisors responsible for profiling Spring JPA repositories.
 */
public class JpaRepositoryAdvisorFactoryBean extends AbstractSpringAdvisorFactoryBean {

    private final MetricService metricService;

    /**
     * Create a new bean.
     * @param metricService Metric service.
     */
    public JpaRepositoryAdvisorFactoryBean(final MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    protected Object getAspect() {
        return new SpringRepositoryProfiler(metricService);
    }

    @Override
    protected String getName() {
        return "springRepositoryProfiler";
    }

}
