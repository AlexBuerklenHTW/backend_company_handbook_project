package com.example.unternehmenshandbuch.mapper;

import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.controller.dto.ArticleStatusEditingAndVersionDto;
import com.example.unternehmenshandbuch.model.Article;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {

    public ArticleResponseDto mapToDto(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleResponseDto.builder()
                .publicId(article.getPublicId())
                .description(article.getDescription())
                .title(article.getTitle())
                .content(article.getContent())
                .version(article.getVersion())
                .status(article.getStatus())
                .editedBy(article.getEditedBy())
                .isEditable(article.getIsEditable())
                .isSubmitted(article.getIsSubmitted())
                .denyText(article.getDenyText())
                .build();
    }

    public List<ArticleResponseDto> mapToDtoList(List<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            return null;
        }

        return articles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ArticleStatusEditingAndVersionDto mapToStatusEditingAndVersion(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleStatusEditingAndVersionDto.builder()
                .editedBy(article.getEditedBy())
                .version(article.getVersion())
                .build();
    }
}
