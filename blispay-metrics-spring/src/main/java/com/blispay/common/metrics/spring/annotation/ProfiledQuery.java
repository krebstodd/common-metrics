package com.blispay.common.metrics.spring.annotation;

import com.blispay.common.metrics.model.call.ds.DsAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation type ProfiledQuery.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ProfiledQuery {

    /**
     * Name of the metric associated with the profiled query.
     * @return name.
     */
    String name();

    /**
     * Name of the schema the query will execute in.
     * @return schema.
     */
    String schema();

    /**
     * Name of the table the query will execute in.
     * @return table.
     */
    String table();

    /**
     * General action the query takes (INSERT,UPDATE,CREATE,DELETE).
     * @return action.
     */
    DsAction action();

}
