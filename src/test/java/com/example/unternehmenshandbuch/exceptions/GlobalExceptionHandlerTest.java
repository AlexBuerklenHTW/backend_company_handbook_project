package com.example.unternehmenshandbuch.exceptions;

import com.example.unternehmenshandbuch.exception.ArticleValidationException;
import com.example.unternehmenshandbuch.exception.GlobalExceptionHandler;
import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	public void testHandleArticleValidationException() {
		ArticleValidationException ex = new ArticleValidationException("Validation error");
		ResponseEntity<String> response = handler.handleArticleValidationException(ex);

		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo("Validation error");
	}

	@Test
	public void testHandleResourceNotFoundException() {
		ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
		ResponseEntity<String> response = handler.handleResourceNotFoundException(ex);

		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isEqualTo("Resource not found");
	}

	@Test
	public void testHandleValidationExceptions() {
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
		when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
		ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		Map<String, String> expectedErrors = new HashMap<>();
		expectedErrors.put("fieldName", "defaultMessage");
		assertThat(response.getBody()).isEqualTo(expectedErrors);
	}

	@Test
	public void testHandleGlobalException() {
		Exception ex = new Exception("Internal server error");
		ResponseEntity<String> response = handler.handleGlobalException(ex);

		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isEqualTo("Internal server error");
	}

	@Test
	public void testHandleIllegalArgumentException() {
		IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
		ResponseEntity<String> response = handler.handleIllegalArgumentException(ex);

		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo("Invalid argument");
	}

	@Test
	public void testHandleUsernameNotFoundException() {
		UsernameNotFoundException ex = new UsernameNotFoundException("Username not found");
		ResponseEntity<String> response = handler.handleUsernameNotFoundException(ex);

		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isEqualTo("Username not found");
	}
}
