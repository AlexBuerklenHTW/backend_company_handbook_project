package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.config.SecurityConfig;
import com.example.unternehmenshandbuch.controller.dto.LoginForm;
import com.example.unternehmenshandbuch.model.AppUser;
import com.example.unternehmenshandbuch.persistence.AppUserRepository;
import com.example.unternehmenshandbuch.service.AppUserDetailsServiceImpl;
import com.example.unternehmenshandbuch.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@Import(SecurityConfig.class)
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private AppUserDetailsServiceImpl appUserDetailsServiceImpl;

	@MockBean
	private AppUserRepository repository;

	private AppUser user;
	private LoginForm loginForm;
	private UserDetails userDetails;

	@BeforeEach
	public void setUp() {
		user = new AppUser();
		user.setUsername("testuser");
		user.setPassword("password");
		user.setRole("USER");

		loginForm = LoginForm.builder().username("testuser").password("password").build();

		userDetails = new User(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	@WithMockUser
	public void testAuthenticateAndGetToken_Success() throws Exception {
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				"testuser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");
		when(appUserDetailsServiceImpl.loadUserByUsername(any())).thenReturn(userDetails);

		mockMvc.perform(post("/authenticate")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"username\": \"testuser\", \"password\": \"password\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("token"));
	}

	@Test
	@WithMockUser
	public void testAuthenticateAndGetToken_Failure() throws Exception {
		when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Invalid login credentials"));

		mockMvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON).content("{\"username\": \"testuser\", \"password\": \"password\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	public void testCreateUser_Success() throws Exception {
		when(repository.findByUsername(any())).thenReturn(Optional.empty());
		when(repository.save(any(AppUser.class))).thenReturn(user);

		mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content("{\"username\": \"testuser\", \"password\": \"password\"}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser")).andExpect(jsonPath("$.role").value("USER"));
	}

	@Test
	@WithMockUser
	public void testCreateUser_UsernameExists() throws Exception {
		when(repository.findByUsername(any())).thenReturn(Optional.of(user));

		mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content("{\"username\": \"testuser\", \"password\": \"password\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser
	public void testAuthenticateAndGetToken_UserNotFoundException() throws Exception {
		when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Invalid login credentials"));

		mockMvc.perform(post("/authenticate")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"username\": \"testuser\", \"password\": \"password\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("Invalid login credentials"));
	}

	@Test
	@WithMockUser
	public void testCreateUser_UsernameAlreadyExists() throws Exception {
		when(repository.findByUsername(any())).thenReturn(Optional.of(user));

		mockMvc.perform(post("/register")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"username\": \"testuser\", \"password\": \"password\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Username already exists"));
	}

	@Test
	@WithMockUser
	public void testAuthenticateAndGetToken_Failure_NotAuthenticated() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password");
		when(authenticationManager.authenticate(any())).thenReturn(authentication);

		mockMvc.perform(post("/authenticate")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"username\": \"testuser\", \"password\": \"wrongpassword\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("Invalid login credentials"));
	}

	@Test
	@WithMockUser
	public void testCreateUser_NoRole() throws Exception {
		when(repository.findByUsername(any())).thenReturn(Optional.empty());
		when(repository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved user

		mockMvc.perform(post("/register")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"username\": \"testuser\", \"password\": \"password\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.role").value("USER"));
	}

}
