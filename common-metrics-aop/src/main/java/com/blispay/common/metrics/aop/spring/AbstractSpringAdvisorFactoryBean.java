package com.blispay.common.metrics.aop.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.SingletonMetadataAwareAspectInstanceFactory;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

/**
 * Base class responsible for converting aspectj aspect objects into spring {@link org.springframework.aop.Advisor} beans.
 */
public abstract class AbstractSpringAdvisorFactoryBean implements FactoryBean<Advisor> {

    /**
     * Get the aspectj annotated object.
     * @return Aspect object.
     */
    protected abstract Object getAspect();

    /**
     * Get the name to use for the advisor bean.
     * @return Bean name.
     */
    protected abstract String getName();

    @Override
    public Advisor getObject() throws Exception {
        final List<Advisor> advisors = new ReflectiveAspectJAdvisorFactory()
                .getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(getAspect(), getName()));

        if (advisors.size() != 1) {
            throw new IllegalStateException(String.format("Aspect [%s] produces more than 1 advisor.", getName()));
        }

        return advisors.get(0);
    }

    @Override
    public Class<?> getObjectType() {
        return Advisor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
