package com.wsgc.gcp.visual.search;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.ProductSearchResults.Result;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageFileNotFoundException;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class VisualSearchController {

	private final StorageService storageService;

	public static String PRODUCT_SET_ID = "we-product-set";

	public static String REGION_NAME = "asia-east1";

	public static String CSV_FILE_GS_LOCATION = "gs://psi-vision-api-bucket/create-product-set.csv";

	public static String PROJECT_ID = "wsi-product-vision-search";

	public static String BUCKET_NAME = "psi-vision-api-bucket";

	public static String GOOGLE_PRODUCT_CATEGORY = "homegoods-v2";

	@Autowired
	public VisualSearchController(StorageService storageService) {
		this.storageService = storageService;
	}

	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
								   RedirectAttributes redirectAttributes, final Model model) throws Exception {

		storageService.store(file);
		final Path path = storageService.load(file.getOriginalFilename());
		final List<Result> results = getSimilarProductsFile(PROJECT_ID, REGION_NAME,
				PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, path.toString(), "");
		model.addAttribute("results", results.stream() //
				.map(i -> new VisionSearchResult(i.getProduct().getDisplayName(), "2 USD",
						i.getImage())) //
				.collect(Collectors.toList()));
		return ResponseEntity.ok().header("Content-Type", "application/json") //
		.body(new Gson().toJson(results.stream() //
				.map(i -> new VisionSearchResult(i.getProduct().getDisplayName(), "2 USD",
						i.getImage())) //
				.collect(Collectors.toList())));
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	public static List<Result> getSimilarProductsFile(
			String projectId,
			String computeRegion,
			String productSetId,
			String productCategory,
			String filePath,
			String filter)
			throws IOException {
		try (ImageAnnotatorClient queryImageClient = ImageAnnotatorClient.create()) {

			// Get the full path of the product set.
			String productSetPath =
					ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

			// Read the image as a stream of bytes.
			File imgPath = new File(filePath);
			byte[] content = Files.readAllBytes(imgPath.toPath());

			// Create annotate image request along with product search feature.
			Feature featuresElement = Feature.newBuilder().setType(Type.PRODUCT_SEARCH).setMaxResults(50).build();
			// The input image can be a HTTPS link or Raw image bytes.
			// Example:
			// To use HTTP link replace with below code
			//  ImageSource source = ImageSource.newBuilder().setImageUri(imageUri).build();
			//  Image image = Image.newBuilder().setSource(source).build();
			Image image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();
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

			List<Result> similarProducts =
					response.getResponses(0).getProductSearchResults().getResultsList();
			System.out.println("Similar Products: ");
			for (Result product : similarProducts) {
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
