package com.wsgc.gcp.visual.search;

import java.util.List;
import java.util.Objects;

import static com.wsgc.gcp.visual.search.VisualSearchController.BUCKET_NAME;

public class ProductImages {

    private final List<String> images;

    public ProductImages(final List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public  void addImage(final String imageId){
        this.getImages().add("https://storage.googleapis.com/" + BUCKET_NAME + "/" + imageId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductImages that = (ProductImages) o;
        return Objects.equals(images, that.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(images);
    }

    @Override
    public String toString() {
        return "ProductImages{" +
                "images=" + images +
                '}';
    }
}
