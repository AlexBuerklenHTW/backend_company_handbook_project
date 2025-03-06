package com.example.unternehmenshandbuch.mapper;

import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.controller.dto.ArticleStatusEditingAndVersionDto;
import com.example.unternehmenshandbuch.model.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleMapperTest {

	private ArticleMapper articleMapper;
	private Article article;

	@BeforeEach
	public void setUp() {
		articleMapper = new ArticleMapper();

		article = Article.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.isSubmitted(true)
				.build();
	}

	@Test
	public void testMapToDto_NullArticle() {
		ArticleResponseDto dto = articleMapper.mapToDto(null);
		assertThat(dto).isNull();
	}

	@Test
	public void testMapToDto_ValidArticle() {
		ArticleResponseDto dto = articleMapper.mapToDto(article);

		assertThat(dto).isNotNull();
		assertThat(dto.getPublicId()).isEqualTo("test-id");
		assertThat(dto.getTitle()).isEqualTo("Test Title");
		assertThat(dto.getDescription()).isEqualTo("Test Description");
		assertThat(dto.getContent()).isEqualTo("Test Content");
		assertThat(dto.getVersion()).isEqualTo(1);
		assertThat(dto.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
		assertThat(dto.getEditedBy()).isEqualTo("testUser");
		assertThat(dto.getIsSubmitted()).isEqualTo(true);
	}

	@Test
	public void testMapToDtoList_NullList() {
		List<ArticleResponseDto> dtos = articleMapper.mapToDtoList(null);
		assertThat(dtos).isNull();
	}

	@Test
	public void testMapToDtoList_EmptyList() {
		List<ArticleResponseDto> dtos = articleMapper.mapToDtoList(Collections.emptyList());
		assertThat(dtos).isNull();
	}

	@Test
	public void testMapToDtoList_ValidList() {
		List<ArticleResponseDto> dtos = articleMapper.mapToDtoList(Collections.singletonList(article));

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(1);

		ArticleResponseDto dto = dtos.get(0);

		assertThat(dto.getPublicId()).isEqualTo("test-id");
		assertThat(dto.getTitle()).isEqualTo("Test Title");
		assertThat(dto.getDescription()).isEqualTo("Test Description");
		assertThat(dto.getContent()).isEqualTo("Test Content");
		assertThat(dto.getVersion()).isEqualTo(1);
		assertThat(dto.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
		assertThat(dto.getEditedBy()).isEqualTo("testUser");
		assertThat(dto.getIsSubmitted()).isEqualTo(true);
	}
	@Test
	public void testMapToStatusEditingAndVersion_NullArticle() {
		ArticleStatusEditingAndVersionDto dto = articleMapper.mapToStatusEditingAndVersion(null);
		assertThat(dto).isNull();
	}

	@Test
	public void testMapToStatusEditingAndVersion_ValidArticle() {
		ArticleStatusEditingAndVersionDto dto = articleMapper.mapToStatusEditingAndVersion(article);

		assertThat(dto).isNotNull();
		assertThat(dto.getEditedBy()).isEqualTo("testUser");
		assertThat(dto.getVersion()).isEqualTo(1);
	}

}
