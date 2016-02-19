package com.blispay.common.metrics.model.call.http;

import com.blispay.common.metrics.model.call.Resource;

public class HttpResource extends Resource {

    private final String url;

    private HttpResource(final String url) {
        this.url = url;
    }

    @Override
    public String getValue() {
        return null;
    }

    public static HttpResource fromUrl(final String url) {
        return new HttpResource(url);
    }

}
