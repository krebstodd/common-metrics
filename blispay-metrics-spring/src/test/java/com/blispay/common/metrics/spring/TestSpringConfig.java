package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Optional;

@Configuration
@EnableAspectJAutoProxy
public class TestSpringConfig {

    /**
     * Env property to have profiled beans throw exceptions.
     */
    public static final String THROW_EXCEPTIONS_PROP = "throwExceptions";

    @Bean
    public MetricService metricService() {
        return new MetricService("test-service");
    }

    /**
     * Build a test profiled bean that is configured to throw exceptions dictated by the environment.
     * @param environment Spring environment.
     * @return Profiled repository implementation.
     */
    @Bean
    public MethodExecutionProfilerTest.ProfiledClass profiledClass(final Environment environment) {
        if (Boolean.valueOf(environment.getProperty(THROW_EXCEPTIONS_PROP))) {
            return new MethodExecutionProfilerTest.ProfiledClass(Optional.of(new RuntimeException("Some exception.")));
        } else {
            return new MethodExecutionProfilerTest.ProfiledClass();
        }
    }

    /**
     * Build a test repository that is configured to throw exceptions dictated by the environment.
     * @param environment Spring environment.
     * @return Profiled repository implementation.
     */
    @Bean
    public QueryExecutionProfilerTest.ProfiledRepository testRepository(final Environment environment) {
        if (Boolean.valueOf(environment.getProperty(THROW_EXCEPTIONS_PROP))) {
            return new QueryExecutionProfilerTest.ProfiledRepositoryImpl(Optional.of(new RuntimeException("Some exception.")));
        } else {
            return new QueryExecutionProfilerTest.ProfiledRepositoryImpl();
        }
    }

    @Bean
    public MethodExecutionProfiler methodExecutionProfiler(final MetricService service) {
        return new MethodExecutionProfiler(service);
    }

    @Bean
    public QueryExecutionProfiler queryExecutionProfiler(final MetricService service) {
        return new QueryExecutionProfiler(service);
    }

    public static AnnotationConfigApplicationContext buildContext() {
        return buildContext(Boolean.FALSE);
    }

    /**
     * Build a new test context w/ this class as it's primary configuration.
     * @param throwExceptions Throw exceptions from the profiled beans.
     * @return A spring context containing all beans defined in this class.
     */
    public static AnnotationConfigApplicationContext buildContext(final Boolean throwExceptions) {

        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

        ctx.register(TestSpringConfig.class);
        ctx.getEnvironment().getPropertySources().addLast(new PropertySource<String>("testSource") {

            @Override
            public Object getProperty(final String name) {
                if (THROW_EXCEPTIONS_PROP.equals(name)) {
                    return throwExceptions;
                } else {
                    return null;
                }
            }

        });

        ctx.refresh();
        ctx.start();

        return ctx;
    }

}