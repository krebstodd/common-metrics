package com.blispay.common.metrics.model.call.ds;

import com.blispay.common.metrics.model.call.Resource;

/**
 * Class DsResource.
 */
public class DsResource extends Resource {

    private final String resourceName;

    /**
     * Constructs DsResource.
     *
     * @param schema schema.
     * @param table table.
     */
    public DsResource(final String schema, final String table) {
        this.resourceName = schema.toLowerCase() + "." + table.toLowerCase();
    }

    @Override
    public String getValue() {
        return resourceName;
    }

    /**
     * Method fromSchemaTable.
     *
     * @param schema schema.
     * @param table table.
     * @return return value.
     */
    public static DsResource fromSchemaTable(final String schema, final String table) {
        return new DsResource(schema, table);
    }

}
