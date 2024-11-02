package com.example.unternehmenshandbuch.exception;


import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class ArticleValidationException extends RuntimeException {
    public ArticleValidationException(String message) {
        super(message);
    }

    public static void validateArticleRequestDto(ArticleRequestDto articleDto) {
        if (isEmpty(articleDto.getTitle()) || isEmpty(articleDto.getDescription())
                || isEmpty(articleDto.getContent()) || isEmpty(articleDto.getEditedBy()) || isEmpty(articleDto.getPublicId())
                || isEmpty(articleDto.getStatus()) || isEmpty(articleDto.getIsEditable()) || isEmpty(articleDto.getVersion())) {
            throw new ArticleValidationException("ArticleRequestDto or its fields must not be null or empty");
        }
    }

    public static void validateArticle(Article article) {
        if (isEmpty(article.getTitle()) || isEmpty(article.getDescription())
                || isEmpty(article.getContent()) || isEmpty(article.getEditedBy()) || isEmpty(article.getPublicId())
                || isEmpty(article.getStatus()) || isEmpty(article.getIsEditable()) || isEmpty(article.getVersion())) {
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

    private static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        return false;
    }
}
