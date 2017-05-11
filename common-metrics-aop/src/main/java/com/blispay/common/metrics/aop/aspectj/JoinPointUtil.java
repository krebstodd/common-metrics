package com.blispay.common.metrics.aop.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * Utility methods for dealing with aspectj join point objects.
 */
public final class JoinPointUtil {

    private JoinPointUtil() {}

    /**
     * Get the class or interface that declares the target method.
     *
     * @param joinPoint Join point.
     * @return Class or interface declaring join point target method.
     */
    public static Class<?> getDeclaringClass(final JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass();
    }


    /**
     * Get the type of object the join point is actually targeting.
     *
     * @param joinPoint Join point.
     * @return Class or interface of the target object.
     */
    public static Class<?> getTargetClass(final JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass();
    }

    /**
     * Get the name of the method being targeted.
     *
     * @param joinPoint Join point.
     * @return Method name.
     */
    public static String getMethodName(final JoinPoint joinPoint) {
        return getMethod(joinPoint).getName();
    }

    /**
     * Get the method being targeted.
     *
     * @param joinPoint Join point.
     * @return Method object.
     */
    public static Method getMethod(final JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    /**
     * Get a method level annotation from a join point.
     *
     * <p>http://stackoverflow.com/questions/6604428/get-annotated-parameters-inside-a-pointcut</p>
     *
     * @param joinPoint The join point to parse.
     * @param type Annotation type class
     * @param <T> Annotation instance type
     * @return Annotation instance
     */
    public static <T> Optional<T> getAnnotation(final JoinPoint joinPoint, final Class<T> type) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        return Arrays.asList(signature.getMethod().getAnnotations()).stream().filter(type::isInstance).findAny().map(ann -> (T) ann);
    }

}
