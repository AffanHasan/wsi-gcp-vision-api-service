package com.wsgc.gcp.visual.search.util;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class SearchUtility {

    public static List<ProductSearchResults.Result> getSimilarProducts(
            String projectId,
            String computeRegion,
            String productSetId,
            String productCategory,
            String filePath,
            String imageURL,
            String filter)
            throws IOException {
        try (ImageAnnotatorClient queryImageClient = ImageAnnotatorClient.create()) {

            // Get the full path of the product set.
            String productSetPath =
                    ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

            // Create annotate image request along with product search feature.
            Feature featuresElement = Feature.newBuilder().setType(Feature.Type.PRODUCT_SEARCH).setMaxResults(50).build();

            final Image image;
            if (null == imageURL) {
                // Read the image as a stream of bytes.
                File imgPath = new File(filePath);
                byte[] content = Files.readAllBytes(imgPath.toPath());
                image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();
            } else {
                ImageSource source = ImageSource.newBuilder().setImageUri(imageURL).build();
                image = Image.newBuilder().setSource(source).build();
            }

            ImageContext imageContext =
                    ImageContext.newBuilder()
                            .setProductSearchParams(
                                    ProductSearchParams.newBuilder()
                                            .setProductSet(productSetPath)
                                            .addProductCategories(productCategory)
                                            .setFilter(filter))
                            .build();

            AnnotateImageRequest annotateImageRequest =
                    AnnotateImageRequest.newBuilder()
                            .addFeatures(featuresElement)
                            .setImage(image)
                            .setImageContext(imageContext)
                            .build();
            List<AnnotateImageRequest> requests = Arrays.asList(annotateImageRequest);

            // Search products similar to the image.
            BatchAnnotateImagesResponse response = queryImageClient.batchAnnotateImages(requests);

            List<ProductSearchResults.Result> similarProducts =
                    response.getResponses(0).getProductSearchResults().getResultsList();
            System.out.println("Similar Products: ");
            for (ProductSearchResults.Result product : similarProducts) {
                System.out.println(String.format("\nProduct name: %s", product.getProduct().getName()));
                System.out.println(
                        String.format("Product display name: %s", product.getProduct().getDisplayName()));
                System.out.println(
                        String.format("Product description: %s", product.getProduct().getDescription()));
                System.out.println(String.format("Score(Confidence): %s", product.getScore()));
                System.out.println(String.format("Image name: %s", product.getImage()));
            }
            return similarProducts;
        }
    }
}
