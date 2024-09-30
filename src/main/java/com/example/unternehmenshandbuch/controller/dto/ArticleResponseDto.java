package com.example.unternehmenshandbuch.controller.dto;


import com.example.unternehmenshandbuch.model.Article;
import lombok.Builder;
import lombok.Data;


import java.util.List;

@Data
@Builder
public class ArticleResponseDto {

    private String publicId;
    private String description;
    private String title;
    private String content;
    private Integer version;
    private Article.ArticleStatus status;
    private String editedBy;
    private List<ArticleResponseDto> versions;
}
