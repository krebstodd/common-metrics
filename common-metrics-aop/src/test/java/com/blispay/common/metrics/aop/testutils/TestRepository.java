package com.blispay.common.metrics.aop.testutils;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Test jpa repository for use in testing {@link com.blispay.common.metrics.aop.spring.SpringRepositoryProfiler}.
 */
@Repository
public interface TestRepository extends JpaRepository<Object, String> {

    /**
     * Some custom method.
     *
     * @param argument Arg.
     * @return Return.
     */
    Object myCustomMethod(String argument);

}
