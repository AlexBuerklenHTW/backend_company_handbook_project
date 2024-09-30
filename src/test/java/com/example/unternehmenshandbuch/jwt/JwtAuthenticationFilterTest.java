package com.example.unternehmenshandbuch.jwt;

import com.example.unternehmenshandbuch.config.JwtAuthenticationFilter;
import com.example.unternehmenshandbuch.service.AppUserDetailsServiceImpl;
import com.example.unternehmenshandbuch.service.JwtService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

	@InjectMocks
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	private JwtService jwtService;

	@Mock
	private AppUserDetailsServiceImpl appUserDetailsServiceImpl;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockFilterChain filterChain;

	@BeforeEach
	public void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testDoFilter_NoAuthHeader() throws ServletException, IOException {
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	public void testDoFilter_InvalidAuthHeader() throws ServletException, IOException {
		request.addHeader("Authorization", "InvalidHeader");
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	public void testDoFilter_ValidToken() throws ServletException, IOException {
		request.addHeader("Authorization", "Bearer validToken");

		UserDetails userDetails = new User("testuser", "password", Collections.singletonList(() -> "ROLE_USER"));
		when(jwtService.extractUsername(anyString())).thenReturn("testuser");
		when(appUserDetailsServiceImpl.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(jwtService.isTokenValid(anyString())).thenReturn(true);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		assertThat(authentication).isNotNull();
		assertThat(authentication.getName()).isEqualTo("testuser");
		assertThat(authentication.getAuthorities()).hasSize(1);
		assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
	}

	@Test
	public void testDoFilter_ValidToken_NotAuthenticated() throws ServletException, IOException {
		request.addHeader("Authorization", "Bearer validToken");

		when(jwtService.extractUsername(anyString())).thenReturn("testuser");
		when(appUserDetailsServiceImpl.loadUserByUsername(anyString())).thenReturn(null);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	public void testDoFilter_InvalidToken() throws ServletException, IOException {
		request.addHeader("Authorization", "Bearer invalidToken");

		when(jwtService.extractUsername(anyString())).thenReturn("testuser");
		when(appUserDetailsServiceImpl.loadUserByUsername(anyString())).thenReturn(null);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}
}
