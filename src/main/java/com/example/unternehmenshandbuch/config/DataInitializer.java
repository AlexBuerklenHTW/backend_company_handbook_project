package com.example.unternehmenshandbuch.config;

import com.example.unternehmenshandbuch.model.AppUser;
import com.example.unternehmenshandbuch.persistence.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DataInitializer {

	private final PasswordEncoder passwordEncoder;

	private final AppUserRepository appUserRepository;

	public DataInitializer(PasswordEncoder passwordEncoder, AppUserRepository appUserRepository) {
		this.passwordEncoder = passwordEncoder;
		this.appUserRepository = appUserRepository;
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			if (appUserRepository.findByUsername("admin").isEmpty()) {
				AppUser admin = new AppUser();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setRole("ADMIN");
				appUserRepository.save(admin);
			}

			if (appUserRepository.findByUsername("user").isEmpty()) {
				AppUser user = new AppUser();
				user.setUsername("user");
				user.setPassword(passwordEncoder.encode("user"));
				user.setRole("USER");
				appUserRepository.save(user);
			}
		};
	}
}
