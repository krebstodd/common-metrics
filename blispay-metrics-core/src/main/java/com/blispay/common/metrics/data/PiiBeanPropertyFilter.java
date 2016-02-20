package com.blispay.common.metrics.data;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

public class PiiBeanPropertyFilter extends SimpleBeanPropertyFilter{

    protected final PiiFieldPredicate isPii;

    public PiiBeanPropertyFilter(final PiiFieldPredicate piiFieldPredicate) {
        this.isPii = piiFieldPredicate;
    }

    protected boolean include(BeanPropertyWriter writer) {
        return !this.isPii.test(writer.getName());
    }

    protected boolean include(PropertyWriter writer) {
        return !this.isPii.test(writer.getName());
    }

}
