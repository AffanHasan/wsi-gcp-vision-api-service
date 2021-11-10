package com.wsgc.gcp.visual.search;

import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageProperties;
import com.wsgc.gcp.visual.search.uploadingfiles.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class GCPVisionAPIService {

	public static void main(String[] args) {
		SpringApplication.run(GCPVisionAPIService.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
