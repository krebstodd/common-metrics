package com.blispay.common.metrics.spring.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public final class JoinPointUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JoinPointUtil.class);

    private JoinPointUtil() {

    }

    public static Class<?> getDeclaringClass(final JoinPoint jPoint) {
        return ((MethodSignature) jPoint.getSignature()).getMethod().getDeclaringClass();
    }

    public static String getMethodName(final JoinPoint jPoint) {
        return getMethod(jPoint).getName();
    }

    public static Method getMethod(final JoinPoint jPoint) {
        return ((MethodSignature) jPoint.getSignature()).getMethod();
    }

    /**
     * Get a method level annotation from a join point.
     *
     * <p>http://stackoverflow.com/questions/6604428/get-annotated-parameters-inside-a-pointcut</p>
     *
     * @param jPoint The join point to parse.
     * @param type Annotation type class
     * @param <T> Annotation instance type
     * @return Annotation instance
     */
    public static <T> Optional<T> getAnnotation(final JoinPoint jPoint, final Class<T> type) {
        final MethodSignature signature = (MethodSignature) jPoint.getSignature();
//        final String methodName = signature.getMethod().getName();
//        final Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();

        return Arrays.asList(signature.getMethod().getAnnotations())
                .stream()
                .filter(type::isInstance)
                .findAny()
                .map(ann -> (T) ann);

//
//
//        try {
//             return Arrays.asList(jPoint.getTarget().getClass().getMethod(methodName, parameterTypes).getAnnotations())
//                     .stream()
//                     .peek(ann -> System.out.println("FOUND ANNOT " + ann.getClass()))
//                     .filter(type::isInstance)
//                     .map(ann -> (T) ann)
//                     .findAny();
//
//        } catch (NoSuchMethodException e) {
//            LOG.error("Unable to locate [{}] annotation on join point method [{}].", type, methodName, e);
//            return Optional.empty();
//        }
    }
}
