package com.wsgc.gcp.visual.search;

import static com.wsgc.gcp.visual.search.util.SearchUtility.*;
import com.google.cloud.vision.v1.ProductSearchResults.Result;
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

import java.nio.file.Path;
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

	@PostMapping("/url")
	@ResponseBody
	public ResponseEntity<String> handleImageURL(@RequestBody final ImageURLSearchRequest request,
												 final Model model) throws Exception {

		final List<Result> results = getSimilarProducts(PROJECT_ID, REGION_NAME,
				PRODUCT_SET_ID, GOOGLE_PRODUCT_CATEGORY, null, request.getUrl(), "");
		setModel(model, results);
		return ResponseEntity.ok().header("Content-Type", "application/json") //
				.body(new Gson().toJson(results.stream() //
						.map(i -> new VisionSearchResult(i.getProduct().getDisplayName(), "2 USD",
								i.getImage())) //
						.collect(Collectors.toList())));
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
		return ResponseEntity.ok().header("Content-Type", "application/json") //
		.body(new Gson().toJson(results.stream() //
				.map(i -> new VisionSearchResult(i.getProduct().getDisplayName(), "2 USD",
						i.getImage())) //
				.collect(Collectors.toList())));
	}

	private void setModel(final Model model, final List<Result> results) {
		model.addAttribute("results", results.stream() //
				.map(i -> new VisionSearchResult(i.getProduct().getDisplayName(), "2",
						i.getImage())) //
				.collect(Collectors.toList()));
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
