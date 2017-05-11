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

    @Bean
    public MetricService metricService() {
        return new MetricService(UUID.randomUUID().toString());
    }

    @Bean
    public TestRepository testRepository() {
        return new TestRepositoryImpl();
    }

    @Bean
    public TestService testService() {
        return new TestService();
    }

    @Bean
    public Advisor springRepositoryProfiler() throws Exception {
        return new JpaRepositoryAdvisorFactoryBean(metricService()).getObject();
    }
}
