package com.opscore;

import com.opscore.entity.User;
import com.opscore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class OpscoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpscoreApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
		return args -> {

			if (repo.findByUsername("admin").isEmpty()) {

				User user = new User();
				user.setUsername("admin");
				user.setPassword(encoder.encode("1234"));

				repo.save(user);
			}
		};
	}


}
