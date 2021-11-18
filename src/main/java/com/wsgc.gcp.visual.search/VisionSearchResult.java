package com.wsgc.gcp.visual.search;

import static com.wsgc.gcp.visual.search.VisualSearchController.*;
import java.util.Objects;

public class VisionSearchResult {

    private final String pid;
    private final String productName;

    private final String productPrice;

    private final String productImage;

    private final Float score;

    public VisionSearchResult(final String pid, final String productName, final String productPrice,
                              final String productImage,
                              final Float score) {
        this.pid = pid;
        this.productName = productName;
        this.productPrice = productPrice;
/*        this.productImage = "https://storage.googleapis.com/" + BUCKET_NAME + "/" +
                productImage.split("/")[7].replace("-id", ".jpg");*/
                this.productImage = "https://storage.googleapis.com/" + BUCKET_NAME + "/" + productImage + ".jpg";
        this.score = score;
    }

    public String getPid() {
        return pid;
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

    public Float getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisionSearchResult that = (VisionSearchResult) o;
        return Objects.equals(pid, that.pid) && Objects.equals(productName, that.productName) && Objects.equals(productPrice, that.productPrice) && Objects.equals(productImage, that.productImage) && Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, productName, productPrice, productImage, score);
    }

    @Override
    public String toString() {
        return "VisionSearchResult{" +
                "pid='" + pid + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productImage='" + productImage + '\'' +
                ", score=" + score +
                '}';
    }
}
