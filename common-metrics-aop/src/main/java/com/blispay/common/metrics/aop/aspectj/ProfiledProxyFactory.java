package com.blispay.common.metrics.aop.aspectj;

import com.blispay.common.metrics.MetricService;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/**
 * Factory capable of taking an object, applying profiler advice to them, and returning a profiled proxy object. When
 * a profiled method on the proxy is called, transactional metrics will be published.
 */
public class ProfiledProxyFactory {

    private final MetricService metricService;

    public ProfiledProxyFactory(final MetricService metricService) {
        this.metricService = metricService;
    }

    /**
     * Profile only methods on the target object annotated w/ {@link Profiled} annotation.
     * @param target Target object to profile.
     * @param <T> Target type.
     * @return Profiled proxy.
     */
    public <T> T profileAnnotatedMethods(final T target) {
        return proxy(target, new AnnotatedFunctionProfiler(metricService));
    }

    /**
     * Profile all non-private methods on the target object.
     * @param target Target object to profile.
     * @param <T> Target type.
     * @return Profiled proxy.
     */
    public <T> T profileAllMethods(final T target) {
        return proxy(target, new BasicFunctionProfiler(metricService));
    }

    private <T> T proxy(final T target, final Object... aspects) {
        final AspectJProxyFactory factory = new AspectJProxyFactory();
        factory.setTarget(target);
        factory.setProxyTargetClass(true);
        for (final Object aspect : aspects) {
            factory.addAspect(aspect);
        }
        return factory.getProxy();
    }

}
