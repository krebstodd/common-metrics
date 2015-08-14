//package com.blispay.common.metrics.legacy;
//
//import com.blispay.common.metrics.BpMetricService;
//import com.blispay.common.metrics.BpTimer;
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//import org.springframework.aop.Advisor;
//import org.springframework.aop.aspectj.AspectJExpressionPointcut;
//import org.springframework.aop.support.DefaultPointcutAdvisor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//
////CHECK_OFF: FinalClass
//
//public class SpringRepositoryProbe implements MethodInterceptor {
//
//    private static final BpMetricService metricService = BpMetricService.getInstance();
//
//    private static final BpTimer queryTimer =
////            BpMetricService.getInstance().createTimer(SpringRepositoryProbe.class, "executionTimer");
//
//    public SpringRepositoryProbe() {}
//
//    //CHECK_OFF: IllegalThrows
//    @Override
//    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
//        final EventPerformanceMonitor.MetricResolver resolver
//                = monitor.start(methodInvocation.getClass().getName(), methodInvocation.getMethod().getName());
//
//        //CHECK_OFF: IllegalCatch
//        try {
//            final Object result = methodInvocation.proceed();
//            return result;
//        } finally {
//            resolver.done();
//        }
//        //CHECK_OFF: IllegalCatch
//    }
//    //CHECK_ON: IllegalThrows
//
//    @Configuration
//    @EnableAspectJAutoProxy
//    public static final class SpringRepositoryProbeConfiguration    {
//
//        /**
//         * Build a new repository profile. Scans the classpath for spring data repositories and wraps them with
//         * a profiling function.
//         *
//         * @return Pointcut advisor.
//         */
//        @Bean
//        public Advisor repositoryProfiler() {
//            final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
//            pointcut.setExpression("execution(public * org.springframework.data.repository.Repository+.*(..))");
//            return new DefaultPointcutAdvisor(pointcut, new SpringRepositoryProbe());
//        }
//
//    }
//
//}
////CHECK_ON: FinalClass
