package com.example.unternehmenshandbuch.service.dto;

import com.example.unternehmenshandbuch.model.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class ArticleRequestDto {

	private String publicId;

	@Size(max = 500, message = "Description must be less than 500 characters")
	private String description;

	@NotBlank(message = "Title is mandatory")
	@Size(max = 255, message = "Title must be less than 255 characters")
	private String title;

	@NotBlank(message = "Content is mandatory")
	private String content;

	private Integer version;

	private Article.ArticleStatus status;

	private String editedBy;

	private Boolean isEditable;

	private Boolean isSubmitted;
}
