package com.wsgc.gcp.visual.search;

import java.util.Objects;

public class VisionSearchResult {

    private final String productName;

    private final String productPrice;

    private final String productImage;

    public VisionSearchResult( final String productName, final String productPrice, final String productImage) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = "https://storage.googleapis.com/psi-vision-api-bucket/" +
                productImage.split("/")[7].replace("-id", ".jpg");
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisionSearchResult that = (VisionSearchResult) o;
        return Objects.equals(productName, that.productName) && Objects.equals(productPrice, that.productPrice) &&
                Objects.equals(productImage, that.productImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productPrice, productImage);
    }
}
