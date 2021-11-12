package com.wsgc.gcp.visual.search.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageURLSearchRequest {

    private final String url;

    @JsonCreator
    public ImageURLSearchRequest(final @JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
