package com.blispay.common.metrics.legacy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//CHECK_OFF: FinalClass
@Configuration
@EnableAspectJAutoProxy
public class SpringRepositoryMonitor extends ApplicationMonitor {

    /**
     * Build a new repository profile. Scans the classpath for spring data repositories and wraps them with
     * a profiling function.
     *
     * @return Pointcut advisor.
     */
    @Bean
    public Advisor repositoryProfiler() {
        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public * org.springframework.data.repository.Repository+.*(..))");
        return new DefaultPointcutAdvisor(pointcut, new SpringRepositoryProfiler());
    }

    public static final class SpringRepositoryProfiler  implements MethodInterceptor  {

        private static final EventPerformanceMonitor monitor
                = EventPerformanceMonitor.getMonitor(DB_QUERY);

        public SpringRepositoryProfiler() {}

        //CHECK_OFF: IllegalThrows
        @Override
        public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
            final EventPerformanceMonitor.MetricResolver resolver
                    = monitor.start(methodInvocation.getClass().getName(), methodInvocation.getMethod().getName());

            //CHECK_OFF: IllegalCatch
            try {
                final Object result = methodInvocation.proceed();
                return result;
            } finally {
                resolver.done();
            }
            //CHECK_OFF: IllegalCatch
        }
        //CHECK_ON: IllegalThrows

    }

}
//CHECK_ON: FinalClass