package com.example.unternehmenshandbuch.exceptions;

import com.example.unternehmenshandbuch.exception.ArticleValidationException;

import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticleValidationExceptionTest {

	@Test
	public void testValidateArticleRequestDto_ValidDto() {
		ArticleRequestDto validDto = new ArticleRequestDto("Title", "Description", "Content", 1, Article.ArticleStatus.APPROVED, "user");

		assertDoesNotThrow(() -> ArticleValidationException.validateArticleRequestDto(validDto));
	}

	@Test
	public void testValidateArticleRequestDto_NullDto() {
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(null));
	}

	@Test
	public void testValidateArticleRequestDto_EmptyFields() {
		ArticleRequestDto invalidDto1 = new ArticleRequestDto(null, "Description", "Content", 1, Article.ArticleStatus.APPROVED, "user");
		ArticleRequestDto invalidDto2 = new ArticleRequestDto("Title", null, "Content", 1, Article.ArticleStatus.APPROVED, "user");
		ArticleRequestDto invalidDto3 = new ArticleRequestDto("Title", "Description", null, 1, Article.ArticleStatus.APPROVED, "user");
		ArticleRequestDto invalidDto4 = new ArticleRequestDto("", "Description", "Content", 1, Article.ArticleStatus.APPROVED, "user");
		ArticleRequestDto invalidDto5 = new ArticleRequestDto("Title", "", "Content", 1, Article.ArticleStatus.APPROVED, "user");
		ArticleRequestDto invalidDto6 = new ArticleRequestDto("Title", "Description", "", 1, Article.ArticleStatus.APPROVED, "user");

		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(invalidDto1));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(invalidDto2));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(invalidDto3));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(invalidDto4));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(invalidDto5));
		assertThrows(ArticleValidationException.class, () -> ArticleValidationException.validateArticleRequestDto(invalidDto6));
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
