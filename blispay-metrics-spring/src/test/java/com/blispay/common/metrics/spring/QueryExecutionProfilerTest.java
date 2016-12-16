package com.blispay.common.metrics.spring;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.matchers.TransactionDataMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.model.call.Direction;
import com.blispay.common.metrics.model.call.Status;
import com.blispay.common.metrics.model.call.TransactionData;
import com.blispay.common.metrics.model.call.ds.DsAction;
import com.blispay.common.metrics.model.call.ds.DsResource;
import com.blispay.common.metrics.spring.annotation.ProfiledQuery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.repository.CrudRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Class QueryExecutionProfilerTest.
 */
public class QueryExecutionProfilerTest {

    private static final Duration SIMULATED_LATENCY = Duration.ofSeconds(1);

    // CHECK_OFF: JavadocVariable
    // CHECK_OFF: VisibilityModifier
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // CHECK_ON: JavadocVariable
    // CHECK_ON: VisibilityModifier

    /**
     * Method testProfileJpaRepository.
     *
     */
    @Test
    public void testProfileJpaRepository() {
        final ConfigurableListableBeanFactory beanFactory = TestSpringConfig.buildContext().getBeanFactory();

        final ProfiledRepository profiled = beanFactory.getBean(ProfiledRepository.class);
        final MetricService metricService = beanFactory.getBean(MetricService.class);

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        metricService.addEventSubscriber(subscriber);

        profiled.profiledQuery();

        assertEquals(1, subscriber.count());

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                                                                        .setApplication(metricService.getApplicationId())
                                                                        .setGroup(EventGroup.CLIENT_JDBC)
                                                                        .setName(QueryExecutionProfilerTest.ProfiledRepository.queryName)
                                                                        .setType(EventType.TRANSACTION)
                                                                        .setDataMatcher(new TransactionDataMatcher(DsResource.fromSchemaTable(ProfiledRepositoryImpl.schema,
                                                                                                                                              ProfiledRepositoryImpl.table),
                                                                                                                   ProfiledRepositoryImpl.dsAction,
                                                                                                                   Direction.OUTBOUND,
                                                                                                                   Status.success(),
                                                                                                                   1000L))
                                                                        .build();

        assertThat((EventModel<TransactionData, Void>) subscriber.poll(), matcher);
    }

    /**
     * Method testJpaRepositoryThrowsException.
     *
     */
    @Test
    public void testJpaRepositoryThrowsException() {
        final ConfigurableListableBeanFactory beanFactory = TestSpringConfig.buildContext(Boolean.TRUE).getBeanFactory();

        final ProfiledRepository profiled = beanFactory.getBean(ProfiledRepository.class);
        final MetricService metricService = beanFactory.getBean(MetricService.class);

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        metricService.addEventSubscriber(subscriber);

        thrown.expect(RuntimeException.class);
        profiled.profiledQuery();

        final EventMatcher<TransactionData, Void> matcher = EventMatcher.<TransactionData, Void>builder()
                                                                        .setApplication(metricService.getApplicationId())
                                                                        .setGroup(EventGroup.CLIENT_JDBC)
                                                                        .setName(QueryExecutionProfilerTest.ProfiledRepository.queryName)
                                                                        .setType(EventType.TRANSACTION)
                                                                        .setDataMatcher(new TransactionDataMatcher(DsResource.fromSchemaTable(ProfiledRepositoryImpl.schema,
                                                                                                                                              ProfiledRepositoryImpl.table),
                                                                                                                   ProfiledRepositoryImpl.dsAction,
                                                                                                                   Direction.OUTBOUND,
                                                                                                                   Status.error(),
                                                                                                                   1000L))
                                                                        .build();

        assertThat((EventModel<TransactionData, Void>) subscriber.poll(), matcher);
    }

    /**
     * Method testJpaRepositoryNonProfiledQuery.
     *
     */
    @Test
    public void testJpaRepositoryNonProfiledQuery() {
        final ConfigurableListableBeanFactory beanFactory = TestSpringConfig.buildContext().getBeanFactory();

        final ProfiledRepository profiled = beanFactory.getBean(ProfiledRepository.class);
        final MetricService metricService = beanFactory.getBean(MetricService.class);

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        metricService.addEventSubscriber(subscriber);

        profiled.nonProfiledQuery();

        assertEquals(0, subscriber.count());
    }

    /**
     * Interface ProfiledRepository.
     */
    public interface ProfiledRepository extends CrudRepository<Object, Integer>, com.blispay.common.metrics.spring.ProfiledRepository {

        /**
         * Query name.
         */
        String queryName = "doQuery";

        /**
         * Schema name.
         */
        String schema = "test-schema";

        /**
         * Table name.
         */
        String table = "test-table";

        /**
         * Query action.
         */
        DsAction dsAction = DsAction.INSERT;

        /**
         * Method profiledQuery.
         *
         * @return return value.
         */
        @ProfiledQuery(name = queryName, schema = schema, table = table, action = DsAction.INSERT)
        Optional<Integer> profiledQuery();

        /**
         * Method nonProfiledQuery.
         *
         * @return return value.
         */
        Optional<Integer> nonProfiledQuery();

    }

    /**
     * Class ProfiledRepositoryImpl.
     */
    public static class ProfiledRepositoryImpl implements ProfiledRepository {

        private final AtomicBoolean executed = new AtomicBoolean(Boolean.FALSE);

        private final Optional<RuntimeException> ex;

        /**
         * Constructs ProfiledRepositoryImpl.
         */
        public ProfiledRepositoryImpl() {
            this(Optional.empty());
        }

        /**
         * Constructs ProfiledRepositoryImpl.
         *
         * @param exceptionOptional exceptionOptional.
         */
        public ProfiledRepositoryImpl(final Optional<RuntimeException> exceptionOptional) {
            this.ex = exceptionOptional;
        }

        @Override
        public Optional<Integer> profiledQuery() {

            try {
                Thread.sleep(SIMULATED_LATENCY.toMillis());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

            executed.set(Boolean.TRUE);
            ex.ifPresent(
                exception -> {
                    throw exception;
                });

            return Optional.of(1);

        }

        @Override
        public Optional<Integer> nonProfiledQuery() {

            try {
                Thread.sleep(SIMULATED_LATENCY.toMillis());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

            executed.set(Boolean.TRUE);
            ex.ifPresent(
                exception -> {
                    throw exception;
                });

            return Optional.of(1);

        }

        @Override
        public Object findOne(final Integer integer) {
            return null;
        }

        @Override
        public boolean exists(final Integer integer) {
            return false;
        }

        @Override
        public List<Object> findAll() {
            return null;
        }

        @Override
        public List<Object> findAll(final Iterable<Integer> iterable) {
            return null;
        }

        @Override
        public <S extends Object> List<S> save(final Iterable<S> iterable) {
            return null;
        }

        @Override
        public <S extends Object> S save(final S obj) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void delete(final Integer integer) {}

        @Override
        public void delete(final Object obj) {}

        @Override
        public void delete(final Iterable<?> iterable) {}

        @Override
        public void deleteAll() {}

    }

}
