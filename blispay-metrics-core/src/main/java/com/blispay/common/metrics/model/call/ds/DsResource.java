package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.call.Resource;

public class DsResource extends Resource {

    private final String resourceName;

    public DsResource(final String schema, final String table) {
        this.resourceName = schema.toLowerCase() + "." + table.toLowerCase();
    }

    @Override
    public String getValue() {
        return resourceName;
    }

    public static DsResource fromSchemaTable(final String schema, final String table) {
        return new DsResource(schema, table);
    }

}