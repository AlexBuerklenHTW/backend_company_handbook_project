package com.example.unternehmenshandbuch.exception;


import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;

public class ArticleValidationException extends RuntimeException {
	public ArticleValidationException(String message) {
		super(message);
	}

	public static void validateArticleRequestDto(ArticleRequestDto articleDto) {
		if (articleDto == null || isEmpty(articleDto.getTitle()) || isEmpty(articleDto.getDescription()) || isEmpty(articleDto.getContent())) {
			throw new ArticleValidationException("ArticleRequestDto or its fields must not be null or empty");
		}
	}

	public static void validateId(String id) {
		if (id == null || id.trim().isEmpty()) {
			throw new ArticleValidationException("ID must not be null or empty");
		}
	}

	public static void validateApprovalStatus(Article.ArticleStatus status) {
		if (status == null) {
			throw new ArticleValidationException("Approval status must not be null");
		}
	}

	private static boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}
