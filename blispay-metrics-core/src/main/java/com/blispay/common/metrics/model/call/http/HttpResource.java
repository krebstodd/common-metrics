package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Resource;

/**
 * Class HttpResource.
 */
public final class HttpResource extends Resource {

    private final String url;

    /**
     * Constructs HttpResource.
     *
     * @param url url.
     */
    private HttpResource(final String url) {
        this.url = url;
    }

    @Override
    public String getValue() {
        return this.url;
    }

    /**
     * Method fromUrl.
     *
     * @param url url.
     * @return return value.
     */
    public static HttpResource fromUrl(final String url) {
        return new HttpResource(url);
    }

}
