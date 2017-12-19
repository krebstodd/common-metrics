package com.blispay.common.metrics.aop.testutils;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Test jpa repository impl for use in testing {@link com.blispay.common.metrics.aop.spring.SpringRepositoryProfiler}.
 */
public class TestRepositoryImpl implements TestRepository {

    /**
     * Simulated latency on repository method calls.
     */
    public static final Duration LATENCY = Duration.ofMillis(RandomUtils.nextInt(100, 1000));
    
    @Override
    public Object save(final Object obj) {
        sleep(LATENCY);
        
        return new Object();
    }

    @Override
    public <S extends Object> List<S> save(final Iterable<S> iterable) {
        return null;
    }

    @Override
    public Object findOne(final String key) {
        sleep(LATENCY);
        return new Object();
    }

    @Override
    public <S extends Object> S findOne(final Example<S> example) {
        return (S) findOne("");
    }


    @Override
    public boolean exists(final String key) {
        sleep(LATENCY);
        return true;
    }

    @Override
    public <S extends Object> boolean exists(final Example<S> example) {
        return exists("");
    }

    @Override
    public <S extends Object> Page<S> findAll(final Example<S> example, final Pageable pageable) {
        return (Page<S>) findAll();
    }

    @Override
    public List<Object> findAll() {
        sleep(LATENCY);
        return Collections.emptyList();
    }

    @Override
    public List<Object> findAll(final Sort sort) {
        sleep(LATENCY);
        return Collections.emptyList();
    }

    @Override
    public Page<Object> findAll(final Pageable pageable) {
        return null;
    }

    @Override
    public List<Object> findAll(final Iterable<String> iterable) {
        sleep(LATENCY);
        return Collections.emptyList();
    }

    @Override
    public <S extends Object> List<S> findAll(final Example<S> example, final Sort sort) {
        sleep(LATENCY);
        return Collections.emptyList();
    }

    @Override
    public <S extends Object> List<S> findAll(final Example<S> example) {
        sleep(LATENCY);
        return Collections.emptyList();
    }

    @Override
    public long count() {
        sleep(LATENCY);
        return 1;
    }

    @Override
    public <S extends Object> long count(final Example<S> example) {
        return count();
    }

    @Override
    public void delete(final String key) {
        sleep(LATENCY);
    }

    @Override
    public void delete(final Object obj) {
        sleep(LATENCY);
    }

    @Override
    public void delete(final Iterable<?> iterable) {
        sleep(LATENCY);
    }

    @Override
    public void deleteAll() {
        sleep(LATENCY);
    }

    @Override
    public void flush() {
        sleep(LATENCY);
    }

    @Override
    public void deleteInBatch(final Iterable<Object> iterable) {
        sleep(LATENCY);
    }

    @Override
    public void deleteAllInBatch() {
        sleep(LATENCY);
    }

    @Override
    public Object getOne(final String key) {
        sleep(LATENCY);
        return new Object();
    }

    @Override
    public Object saveAndFlush(final Object obj) {
        sleep(LATENCY);
        
        return new Object();
    }

    @Override
    public Object myCustomMethod(final String argument) {
        sleep(LATENCY);
        return new Object();
    }

    private void sleep(final Duration latency) {
        try {
            Thread.sleep(latency.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Duration getLatency() {
        return LATENCY;
    }

    public Object getCurrState() {
        return new Object();
    }

}
