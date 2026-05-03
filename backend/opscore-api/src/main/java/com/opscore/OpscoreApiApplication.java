package com.opscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OpscoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpscoreApiApplication.class, args);
	}

}
