package com.example.unternehmenshandbuch.exceptions;

import com.example.unternehmenshandbuch.exception.ArticleValidationException;

import com.example.unternehmenshandbuch.model.Article;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticleValidationExceptionTest {

	@Test
	public void testValidateArticleRequestDto_NullDto() {
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(null));
	}

	@Test
	public void testValidateId_ValidId() {
		assertDoesNotThrow(() -> ArticleValidationException.validateId("valid-id"));
	}

	@Test
	public void testValidateId_NullOrEmpty() {
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateId(null));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateId(""));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateId("   "));
	}

	@Test
	public void testValidateApprovalStatus_ValidStatus() {
		assertDoesNotThrow(() -> ArticleValidationException.validateApprovalStatus(Article.ArticleStatus.APPROVED));
		assertDoesNotThrow(() -> ArticleValidationException.validateApprovalStatus(Article.ArticleStatus.APPROVED));
	}

	@Test
	public void testValidateApprovalStatus_NullStatus() {
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateApprovalStatus(null));
	}

}
