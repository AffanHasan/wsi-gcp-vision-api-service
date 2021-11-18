package com.wsgc.gcp.visual.search;

import static com.wsgc.gcp.visual.search.util.SearchUtility.*;

import com.google.cloud.vision.v1.ProductSearchClient;
import com.google.cloud.vision.v1.ProductSearchResults.Result;
import com.google.cloud.vision.v1.ReferenceImage;
import com.google.gson.Gson;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageFileNotFoundException;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageService;
import com.wsgc.gcp.visual.search.util.ImageURLSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
public class VisualSearchController {

	private final StorageService storageService;

	public static String PRODUCT_SET_ID = "we-product-set-17-nov-2021-12-59-pm";

	public static String REGION_NAME = "asia-east1";

	public static String BUCKET_NAME = "wsi-vision-api-bucket";

	public static String CSV_FILE_GS_LOCATION = "gs://" + BUCKET_NAME + "/create-product-set.csv";

	public static String PROJECT_ID = "wsi-product-vision-search";


	public static String GOOGLE_PRODUCT_CATEGORY = "homegoods-v2";

	@Autowired
	public VisualSearchController(StorageService storageService) {
		this.storageService = storageService;
	}

	@PostMapping("/url")
	@ResponseBody
	public ResponseEntity<String> handleImageURL(@RequestBody final ImageURLSearchRequest request,
												 final Model model) throws Exception {

		final List<Result> results = getSimilarProducts(PROJECT_ID, REGION_NAME,
				PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, null, request.getUrl(), "");
		setModel(model, results);
		return getResponseEntity(results);
	}

	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
								   RedirectAttributes redirectAttributes, final Model model) throws Exception {

		storageService.store(file);
		final Path path = storageService.load(file.getOriginalFilename());
		final List<Result> results = getSimilarProducts(PROJECT_ID, REGION_NAME,
				PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, path.toString(), null, "");
		setModel(model, results);
		return getResponseEntity(results);
	}

	@GetMapping("/get-product-images")
	@ResponseBody
	public static ResponseEntity<String> getReferenceImagesOfProduct(@RequestParam("pid") final String productId) throws IOException {

		try (ProductSearchClient client = ProductSearchClient.create()) {

			// Get the full path of the product.
			String formattedParent =
					ProductSearchClient.formatProductName(PROJECT_ID, REGION_NAME, productId);
			final ProductImages productImages = new ProductImages(new ArrayList<>());
			for (ReferenceImage image : client.listReferenceImages(formattedParent).iterateAll()) {
				final String imageId = image.getName().substring(image.getName().lastIndexOf('/') + 1);
				productImages.addImage(imageId);
				// Display the reference image information.
				System.out.println(String.format("Reference image name: %s", image.getName()));
				System.out.println(
						String.format(
								"Reference image id: %s",
								image.getName().substring(image.getName().lastIndexOf('/') + 1)));
				System.out.println(String.format("Reference image uri: %s", image.getUri()));
				System.out.println(
						String.format(
								"Reference image bounding polygons: %s \n",
								image.getBoundingPolysList().toString()));
			}
			return ResponseEntity.ok().header("Content-Type", "application/json") //
					.body(new Gson().toJson(productImages));
		}
	}

	private ResponseEntity<String> getResponseEntity(List<Result> results) {
		List<VisionSearchResult> list = results.stream() //
				.map(i -> new VisionSearchResult(i.getProduct().getName().substring(i.getProduct().getName().lastIndexOf('/') + 1)
						, i.getProduct().getDisplayName(), "2",
						i.getImage(), i.getScore())) //
				.collect(Collectors.toList());
		return ResponseEntity.ok().header("Content-Type", "application/json") //
				.body(new Gson().toJson(list));
	}

	private void setModel(final Model model, final List<Result> results) {
		model.addAttribute("results", results.stream() //
				.map(i -> new VisionSearchResult(i.getProduct().getName().substring(i.getProduct().getName().lastIndexOf('/') + 1),
						i.getProduct().getDisplayName(), "2",
						i.getImage(), i.getScore())) //
				.collect(Collectors.toList()));
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
