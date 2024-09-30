package com.example.unternehmenshandbuch.jwt;

import com.example.unternehmenshandbuch.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

	private JwtService jwtService;
	private SecretKey secretKey;

	@BeforeEach
	void setUp() throws Exception {
		jwtService = new JwtService();

		Field secretField = JwtService.class.getDeclaredField("SECRET");
		secretField.setAccessible(true);
		String secret = (String) secretField.get(jwtService);

		byte[] decodedKey = Base64.getDecoder().decode(secret);
		secretKey = Keys.hmacShaKeyFor(decodedKey);
	}

	@Test
	void generateToken_ShouldReturnValidToken() {
		UserDetails userDetails = new User("testuser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

		String token = jwtService.generateToken(userDetails);

		assertNotNull(token);
		assertTrue(jwtService.isTokenValid(token));
	}

	@Test
	void extractUsername_ShouldReturnCorrectUsername() {
		UserDetails userDetails = new User("testuser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		String token = jwtService.generateToken(userDetails);

		String username = jwtService.extractUsername(token);

		assertEquals("testuser", username);
	}

	@Test
	void isTokenValid_ShouldReturnTrueForValidToken() {

		UserDetails userDetails = new User("testuser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		String token = jwtService.generateToken(userDetails);

		boolean isValid = jwtService.isTokenValid(token);

		assertTrue(isValid);
	}

	@Test
	void isTokenValid_ShouldReturnFalseForExpiredToken() {

		String token = Jwts.builder()
				.setSubject("testuser")
				.setExpiration(new Date(System.currentTimeMillis() - 1000))
				.signWith(secretKey)
				.compact();

		assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtService.isTokenValid(token));
	}
}
