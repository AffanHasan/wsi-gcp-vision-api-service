package com.wsgc.gcp.visual.search.util;

import  static com.wsgc.gcp.visual.search.VisualSearchController.*;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SearchUtility {

    public static List<ProductSearchResults.Result> getSimilarProductsTemp(
            String projectId,
            String computeRegion,
            String productSetId,
            String productCategory,
            String imageURL,
            String filter,
            byte[] content)
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
                /*File imgPath = new File(filePath);
                byte[] content = Files.readAllBytes(imgPath.toPath());
                image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();*/
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

    public static List<ProductSearchResults.Result> getSimilarProducts(
            String projectId,
            String computeRegion,
            String productSetId,
            String productCategory,
            String imageURL,
            String filter,
            byte[] content)
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
                /*File imgPath = new File(filePath);
                byte[] content = Files.readAllBytes(imgPath.toPath());
                image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();*/
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

    public static String getSimilarProductsJsonString(
            String projectId,
            String computeRegion,
            String productSetId,
            String productCategory,
            String imageURL,
            String filter,
            byte[] content)
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
                /*File imgPath = new File(filePath);
                byte[] content = Files.readAllBytes(imgPath.toPath());
                image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();*/
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
            /*for (ProductSearchResults.Result product : similarProducts) {
                System.out.println(String.format("\nProduct name: %s", product.getProduct().getName()));
                System.out.println(
                        String.format("Product display name: %s", product.getProduct().getDisplayName()));
                System.out.println(
                        String.format("Product description: %s", product.getProduct().getDescription()));
                System.out.println(String.format("Score(Confidence): %s", product.getScore()));
                System.out.println(String.format("Image name: %s", product.getImage()));
            }*/
            /*for () {


            }*/
            final JsonObject  jo = new Gson().toJsonTree(response) //
            .getAsJsonObject();
            addCustomPropertiesToProductResults(jo.get("responses_") //
                    .getAsJsonArray() //
                    .get(0) //
                    .getAsJsonObject() //
                    .get("productSearchResults_") //
                    .getAsJsonObject() //
                    .get("results_") //
                    .getAsJsonArray());
            jo.get("responses_") //
                    .getAsJsonArray() //
                    .get(0) //
                    .getAsJsonObject() //
                    .get("productSearchResults_") //
            .getAsJsonObject() //
            .get("productGroupedResults_") //
            .getAsJsonArray() //
            .forEach(boundingPolly -> {
                addCustomPropertiesToProductResults(boundingPolly.getAsJsonObject() //
                        .get("results_") //
                        .getAsJsonArray());
            });

            return  jo.toString();
        }
    }

    public static void addCustomPropertiesToProductResults(final JsonArray resultsJsonArray) {
        resultsJsonArray.forEach(product -> {
            final JsonObject productJO = product.getAsJsonObject();
            final JsonObject labelPID = new JsonObject();
            labelPID.addProperty("key_", "pid");
            labelPID.addProperty("value_", fetchProductIdFromProductName(productJO.get("product_").getAsJsonObject().get("name_").getAsString()));

            final JsonObject labelImagePublicURL = new JsonObject();
            labelImagePublicURL.addProperty("key_", "imagePublicURL");
            labelImagePublicURL.addProperty("value_", "https://storage.googleapis.com/"
                    + BUCKET_NAME + "/" +
                    fetchImageIdFromProductImage(productJO.get("image_").getAsString()));
            productJO.get("product_").getAsJsonObject().get("productLabels_") //
                    .getAsJsonArray() //
                    .add(labelImagePublicURL);
            productJO.get("product_").getAsJsonObject().get("productLabels_") //
                    .getAsJsonArray() //
                    .add(labelPID);
        });
    }
}
