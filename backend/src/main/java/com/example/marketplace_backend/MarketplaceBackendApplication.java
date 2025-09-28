package com.example.marketplace_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class MarketplaceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketplaceBackendApplication.class, args);
	}

}
