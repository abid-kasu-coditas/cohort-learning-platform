package com.example.cohortplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CohortPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(CohortPlatformApplication.class, args);
	}

}
