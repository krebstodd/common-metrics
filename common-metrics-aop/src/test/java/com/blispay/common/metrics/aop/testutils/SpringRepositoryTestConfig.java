package com.blispay.common.metrics.aop.testutils;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.aop.spring.JpaRepositoryAdvisorFactoryBean;
import org.springframework.aop.Advisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.UUID;

/**
 * Test spring config for use in testing spring repository profiling.
 */
@Configuration
@EnableAspectJAutoProxy
public class SpringRepositoryTestConfig {

    /**
     * Metric service.
     * @return Metric service.
     */
    @Bean
    public MetricService metricService() {
        return new MetricService(UUID.randomUUID().toString());
    }

    /**
     * Test repo.
     * @return Test repo.
     */
    @Bean
    public TestRepository testRepository() {
        return new TestRepositoryImpl();
    }

    /**
     * Test service.
     * @return Test service.
     */
    @Bean
    public TestService testService() {
        return new TestService();
    }

    /**
     * Advisor.
     * @return Advisor.
     * @throws Exception on error.
     */
    @Bean
    public Advisor springRepositoryProfiler() throws Exception {
        return new JpaRepositoryAdvisorFactoryBean(metricService()).getObject();
    }
}
