package com.example.unternehmenshandbuch.service;

import com.example.unternehmenshandbuch.model.AppUser;
import com.example.unternehmenshandbuch.persistence.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserDetailsServiceImplTest {

	@Mock
	private AppUserRepository repository;

	@InjectMocks
	private AppUserDetailsServiceImpl userDetailsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void loadUserByUsername_UserExists() {
		AppUser appUser = new AppUser();
		appUser.setUsername("testuser");
		appUser.setPassword("password");
		appUser.setRole("USER");
		when(repository.findByUsername("testuser")).thenReturn(Optional.of(appUser));

		UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

		assertNotNull(userDetails);
		assertEquals("testuser", userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().stream()
						   .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
	}

	@Test
	void loadUserByUsername_UserDoesNotExist() {

		when(repository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("nonexistent"));
	}

	@Test
	void getRoles_UserHasNoRole() {

		AppUser appUser = new AppUser();
		appUser.setUsername("testuser");
		appUser.setPassword("password");
		appUser.setRole(null);
		when(repository.findByUsername("testuser")).thenReturn(Optional.of(appUser));

		UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
		String[] roles = userDetails.getAuthorities()
				.stream().map(GrantedAuthority::getAuthority)
				.toArray(String[]::new);

		assertArrayEquals(new String[] { "ROLE_USER" }, roles);
	}

	@Test
	void getRoles_UserHasMultipleRoles() {
		AppUser appUser = new AppUser();
		appUser.setUsername("testuser");
		appUser.setPassword("password");
		appUser.setRole("ADMIN,USER");
		when(repository.findByUsername("testuser")).thenReturn(Optional.of(appUser));

		UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

		assertNotNull(userDetails);
		assertEquals("testuser", userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().stream()
						   .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
		assertTrue(userDetails.getAuthorities().stream()
						   .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
	}
}
