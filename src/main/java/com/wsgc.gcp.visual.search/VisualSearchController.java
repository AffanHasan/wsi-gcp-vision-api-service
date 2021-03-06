package com.wsgc.gcp.visual.search;

import static com.wsgc.gcp.visual.search.util.SearchUtility.*;

import com.google.cloud.vision.v1.ProductSearchClient;
import com.google.cloud.vision.v1.ProductSearchResults;
import com.google.cloud.vision.v1.ReferenceImage;
import com.google.gson.Gson;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageFileNotFoundException;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageService;
import com.wsgc.gcp.visual.search.util.ImageURLSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
public class VisualSearchController {

	private final StorageService storageService;

	public static String WE_PRODUCT_SET_ID = "we-product-set-18-nov-2021-12-49-pm";
	public static String PK_PRODUCT_SET_ID = "pk-product-set";

	public static String REGION_NAME = "asia-east1";

	public static String BUCKET_NAME = "wsi-vision-api-bucket";

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

		final String json = getSimilarProductsJsonString(PROJECT_ID, REGION_NAME,
				WE_PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, request.getUrl(), "", null);
		return getResponseEntity(json);
	}

	@PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
								   @RequestPart("brand") final String brand,
								   RedirectAttributes redirectAttributes, final Model model) throws Exception {
		System.out.println(brand);
		final String json;
		switch (brand.toUpperCase(Locale.ROOT)) {
			case "WE": {
				 json = getSimilarProductsJsonString(PROJECT_ID, REGION_NAME,
						WE_PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, null, "", file.getBytes());
				break;
			}
			case "PK":
			{
				json = getSimilarProductsJsonString(PROJECT_ID, REGION_NAME,
						PK_PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, null, "", file.getBytes());
				break;
			}
			default: {
				json = "{}";
			}
		}
		return getResponseEntity(json);
	}

	@PostMapping("/uploadAndReturnModel")
	public String handleFileUploadAndReturnModel(@RequestParam("file") MultipartFile file,
												   RedirectAttributes redirectAttributes, final Model model) throws Exception {
		final List<ProductSearchResults.Result> results = getSimilarProductsTemp(PROJECT_ID, REGION_NAME,
				WE_PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, null, "", file.getBytes());
		setModel(model, results);
		return "uploadForm";
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

	private ResponseEntity<String> getResponseEntity(final String json) {
/*		List<VisionSearchResult> list = results.stream() //
				.map(i -> new VisionSearchResult(fetchProductIdFromProductName(i.getProduct().getName()),
						i.getProduct().getDisplayName(), "2",
						this.fetchImageIdFromProductImage(i.getImage()), i.getScore())) //
				.collect(Collectors.toList());
		return ResponseEntity.ok().header("Content-Type", "application/json") //
				.body(new Gson().toJson(list));*/
		return ResponseEntity.ok().header("Content-Type", "application/json") //
				.body(json);
	}

	private void setModel(final Model model, final List<ProductSearchResults.Result> results) {
		model.addAttribute("results", results.stream() //
				.map(i -> new VisionSearchResult(this.fetchProductIdFromProductName(i.getProduct().getName()),
						i.getProduct().getDisplayName(), "2",
						this.fetchImageIdFromProductImage(i.getImage()), i.getScore())) //
				.collect(Collectors.toList()));
	}

	public static String fetchProductIdFromProductName(final String productName) {
		return productName.substring(productName.lastIndexOf("/") + 1);
	}

	public static String fetchImageIdFromProductImage(final String imageName) {
		return imageName.substring(imageName.lastIndexOf("/") + 1);
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
