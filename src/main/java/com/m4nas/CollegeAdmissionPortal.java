package com.m4nas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * CollegeAdmissionPortal - Enterprise College Admission System
 * 
 * Main Spring Boot application class that bootstraps the entire user management system.
 * This application provides:
 * - Role-based authentication and authorization (Admin, Teacher, User)
 * - OAuth2 integration with Google and GitHub
 * - Email verification and password recovery
 * - Enterprise-grade security features
 * - Responsive web interface with modern UI/UX
 * 
 * @author UserAuth Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@SpringBootApplication
public class CollegeAdmissionPortal {

	/**
	 * Main method to start the Spring Boot application.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CollegeAdmissionPortal.class, args);
	}

	/**
	 * RestTemplate bean for making HTTP requests to external services.
	 * Used primarily for OAuth2 integration and external API calls.
	 * 
	 * @return RestTemplate instance for HTTP operations
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
