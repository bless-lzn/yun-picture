package com.yupi.lipicture.infrastructure.api.ImageSearch.model;

import lombok.Data;

@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}
