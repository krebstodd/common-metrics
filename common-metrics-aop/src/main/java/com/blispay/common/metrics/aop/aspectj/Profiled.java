package com.blispay.common.metrics.aop.aspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking methods whos execution should be profiled with transactional metrics.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Profiled {

    /**
     * The name field to set on the transactional metric. Defaults to method name if null or empty.
     * @return Metric name.
     */
    String name() default "";

    /**
     * Action string to be set on transactional metric. Defaults to method name if null or empty.
     * @return Action string.
     */
    String action() default "";

    /**
     * Resource string to be set on transactional metric. Defaults to the target class name if null or empty.
     * @return Resource name.
     */
    String resource() default "";

}
