package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.config.SecurityConfig;
import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.exception.ArticleValidationException;
import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import com.example.unternehmenshandbuch.mapper.ArticleMapper;
import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.AppUserDetailsServiceImpl;
import com.example.unternehmenshandbuch.service.ArticleService;
import com.example.unternehmenshandbuch.service.JwtService;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ArticleController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@Import(SecurityConfig.class)
public class ArticleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ArticleService articleService;

	@MockBean
	private AppUserDetailsServiceImpl appUserDetailsServiceImpl;

	@MockBean
	private ArticleMapper articleMapper;

	@MockBean
	private JwtService jwtService;

	private Article article;
	private ArticleResponseDto articleResponseDto;

	@BeforeEach
	public void setUp() {
		article = Article.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.build();

		articleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.build();
	}

	@Test
	@WithMockUser
	public void testCreateArticle_Success() throws Exception {
		when(articleService.createArticle(any(ArticleRequestDto.class))).thenReturn(article);
		when(articleMapper.mapToDto(any(Article.class))).thenReturn(articleResponseDto);

		mockMvc.perform(post("/articles")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"title\": \"Test Title\", \"description\": \"Test Description\", \"content\": \"Test Content\", \"editedBy\": \"testUser\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.publicId").value("test-id"))
				.andExpect(jsonPath("$.title").value("Test Title"))
				.andExpect(jsonPath("$.description").value("Test Description"))
				.andExpect(jsonPath("$.content").value("Test Content"))
				.andExpect(jsonPath("$.version").value(1))
				.andExpect(jsonPath("$.status").value("EDITING"))
				.andExpect(jsonPath("$.editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testGetArticleById_Success() throws Exception {
		when(articleService.getArticleById(anyString())).thenReturn(article);
		when(articleMapper.mapToDto(any(Article.class))).thenReturn(articleResponseDto);

		mockMvc.perform(get("/articles/test-id")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.publicId").value("test-id"))
				.andExpect(jsonPath("$.title").value("Test Title"))
				.andExpect(jsonPath("$.description").value("Test Description"))
				.andExpect(jsonPath("$.content").value("Test Content"))
				.andExpect(jsonPath("$.version").value(1))
				.andExpect(jsonPath("$.status").value("EDITING"))
				.andExpect(jsonPath("$.editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testUpdateArticle_Success() throws Exception {
		Article updatedArticle = Article.builder()
				.publicId("test-id")
				.title("Updated Title")
				.description("Updated Description")
				.content("Updated Content")
				.version(2)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.build();

		ArticleResponseDto updatedArticleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Updated Title")
				.description("Updated Description")
				.content("Updated Content")
				.version(2)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.build();

		when(articleService.updateArticle(anyString(), any(ArticleRequestDto.class))).thenReturn(updatedArticle);
		when(articleMapper.mapToDto(any(Article.class))).thenReturn(updatedArticleResponseDto);

		mockMvc.perform(put("/articles/test-id")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"title\": \"Updated Title\", \"description\": \"Updated Description\", \"content\": \"Updated Content\", \"editedBy\": \"testUser\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.publicId").value("test-id"))
				.andExpect(jsonPath("$.title").value("Updated Title"))
				.andExpect(jsonPath("$.description").value("Updated Description"))
				.andExpect(jsonPath("$.content").value("Updated Content"))
				.andExpect(jsonPath("$.version").value(2))
				.andExpect(jsonPath("$.status").value("EDITING"))
				.andExpect(jsonPath("$.editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testSetApprovalStatus_Success() throws Exception {
		Article approvedArticle = Article.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.APPROVED)
				.editedBy("testUser")
				.build();

		ArticleResponseDto approvedArticleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.APPROVED)
				.editedBy("testUser")
				.build();

		when(articleService.setApprovalStatus(anyString(), anyString(), anyInt(), anyString())).thenReturn(approvedArticle);
		when(articleMapper.mapToDto(any(Article.class))).thenReturn(approvedArticleResponseDto);

		String approvalRequestJson = "{\"status\": \"APPROVED\", \"version\": 1, \"editedBy\": \"testUser\"}";

		mockMvc.perform(put("/articles/test-id/approval")
								.contentType(MediaType.APPLICATION_JSON)
								.content(approvalRequestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("APPROVED"));
	}

	@Test
	@WithMockUser
	public void testGetApprovedArticles_Success() throws Exception {
		Article approvedArticle = Article.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.APPROVED)
				.editedBy("testUser")
				.build();

		ArticleResponseDto approvedArticleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Test Title")
				.description("Test Description")
				.content("Test Content")
				.version(1)
				.status(Article.ArticleStatus.APPROVED)
				.editedBy("testUser")
				.build();

		when(articleService.getApprovedArticles()).thenReturn(Collections.singletonList(approvedArticle));
		when(articleMapper.mapToDtoList(anyList())).thenReturn(Collections.singletonList(approvedArticleResponseDto));

		mockMvc.perform(get("/articles/approved")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].publicId").value("test-id"))
				.andExpect(jsonPath("$[0].title").value("Test Title"))
				.andExpect(jsonPath("$[0].description").value("Test Description"))
				.andExpect(jsonPath("$[0].content").value("Test Content"))
				.andExpect(jsonPath("$[0].version").value(1))
				.andExpect(jsonPath("$[0].status").value("APPROVED"));
	}

	@Test
	@WithMockUser
	public void testGetApprovedArticles_EmptyList() throws Exception {
		when(articleService.getApprovedArticles()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/articles/approved")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@WithMockUser
	public void testCreateArticle_NullTitle() throws Exception {
		String invalidArticleJson = "{\"title\": null, \"description\": \"Test Description\", \"content\": \"Test Content\", \"editedBy\": \"testUser\"}";

		mockMvc.perform(post("/articles")
								.contentType(MediaType.APPLICATION_JSON)
								.content(invalidArticleJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser
	public void testGetArticleById_NotFound() throws Exception {
		when(articleService.getArticleById(anyString())).thenThrow(new ResourceNotFoundException("Article not found for ID: non-existent-id"));

		mockMvc.perform(get("/articles/non-existent-id")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Article not found for ID: non-existent-id"));
	}

	@Test
	@WithMockUser
	public void testUpdateArticle_InvalidId() throws Exception {
		when(articleService.updateArticle(anyString(), any(ArticleRequestDto.class))).thenThrow(new ArticleValidationException("Invalid ID"));

		mockMvc.perform(put("/articles/invalid-id")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"title\": \"Updated Title\", \"description\": \"Updated Description\", \"content\": \"Updated Content\", \"editedBy\": \"testUser\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Invalid ID"));
	}

	@Test
	@WithMockUser
	public void testSetApprovalStatus_InvalidId() throws Exception {
		when(articleService.setApprovalStatus(anyString(), anyString(), anyInt(), anyString()))
				.thenThrow(new ArticleValidationException("ID must not be null or empty"));

		String approvalRequestJson = "{\"status\": \"APPROVED\", \"version\": 1, \"editedBy\": \"testUser\"}";

		mockMvc.perform(put("/articles/invalid-id/approval")
								.contentType(MediaType.APPLICATION_JSON)
								.content(approvalRequestJson))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("ID must not be null or empty"));
	}

	@Test
	@WithMockUser
	public void testGetLatestArticleByPublicId_Success() throws Exception {
		article.setVersion(2);
		article.setStatus(Article.ArticleStatus.APPROVED);

		articleResponseDto.setVersion(2);
		articleResponseDto.setStatus(Article.ArticleStatus.APPROVED);

		when(articleService.getLatestArticleByPublicId(anyString())).thenReturn(article);
		when(articleMapper.mapToDto(article)).thenReturn(articleResponseDto);

		mockMvc.perform(get("/articles/test-id/latest")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.publicId").value("test-id"))
				.andExpect(jsonPath("$.title").value("Test Title"))
				.andExpect(jsonPath("$.description").value("Test Description"))
				.andExpect(jsonPath("$.content").value("Test Content"))
				.andExpect(jsonPath("$.version").value(2))
				.andExpect(jsonPath("$.status").value("APPROVED"))
				.andExpect(jsonPath("$.editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testCreateArticle_MissingTitle() throws Exception {
		String invalidArticleJson = "{\"description\": \"Test Description\", \"content\": \"Test Content\", \"editedBy\": \"testUser\"}";

		mockMvc.perform(post("/articles")
								.contentType(MediaType.APPLICATION_JSON)
								.content(invalidArticleJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser
	public void testGetLatestSubmittedArticleByPublicId_Success() throws Exception {
		Article latestSubmittedArticle = Article.builder()
				.publicId("test-id")
				.title("Latest Submitted Title")
				.description("Latest Submitted Description")
				.content("Latest Submitted Content")
				.version(3)
				.status(Article.ArticleStatus.SUBMITTED)
				.editedBy("testUser")
				.build();

		ArticleResponseDto latestSubmittedArticleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Latest Submitted Title")
				.description("Latest Submitted Description")
				.content("Latest Submitted Content")
				.version(3)
				.status(Article.ArticleStatus.SUBMITTED)
				.editedBy("testUser")
				.build();

		when(articleService.getLatestSubmittedArticleByPublicId(anyString())).thenReturn(latestSubmittedArticle);
		when(articleMapper.mapToDto(any(Article.class))).thenReturn(latestSubmittedArticleResponseDto);

		mockMvc.perform(get("/articles/test-id/latest-submitted")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.publicId").value("test-id"))
				.andExpect(jsonPath("$.title").value("Latest Submitted Title"))
				.andExpect(jsonPath("$.description").value("Latest Submitted Description"))
				.andExpect(jsonPath("$.content").value("Latest Submitted Content"))
				.andExpect(jsonPath("$.version").value(3))
				.andExpect(jsonPath("$.status").value("SUBMITTED"))
				.andExpect(jsonPath("$.editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testGetArticlesEditedByUser_Success() throws Exception {
		Article articleEditedByUser = Article.builder()
				.publicId("test-id")
				.title("User Edited Title")
				.description("User Edited Description")
				.content("User Edited Content")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.build();

		ArticleResponseDto articleEditedByUserResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("User Edited Title")
				.description("User Edited Description")
				.content("User Edited Content")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("testUser")
				.build();

		when(articleService.getArticlesByUserAndStatus(anyString(), any(Article.ArticleStatus.class)))
				.thenReturn(Collections.singletonList(articleEditedByUser));
		when(articleMapper.mapToDtoList(anyList()))
				.thenReturn(Collections.singletonList(articleEditedByUserResponseDto));

		mockMvc.perform(get("/articles/user/testUser")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].publicId").value("test-id"))
				.andExpect(jsonPath("$[0].title").value("User Edited Title"))
				.andExpect(jsonPath("$[0].description").value("User Edited Description"))
				.andExpect(jsonPath("$[0].content").value("User Edited Content"))
				.andExpect(jsonPath("$[0].version").value(1))
				.andExpect(jsonPath("$[0].status").value("EDITING"))
				.andExpect(jsonPath("$[0].editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testGetArticlesByStatusAndRole_Success() throws Exception {
		Article articleByStatusAndRole = Article.builder()
				.publicId("test-id")
				.title("Role Based Title")
				.description("Role Based Description")
				.content("Role Based Content")
				.version(1)
				.status(Article.ArticleStatus.SUBMITTED)
				.editedBy("testUser")
				.build();

		ArticleResponseDto articleByStatusAndRoleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Role Based Title")
				.description("Role Based Description")
				.content("Role Based Content")
				.version(1)
				.status(Article.ArticleStatus.SUBMITTED)
				.editedBy("testUser")
				.build();

		when(articleService.getArticlesByRoleAndStatus(anyString(), anyString()))
				.thenReturn(Collections.singletonList(articleByStatusAndRole));
		when(articleMapper.mapToDtoList(anyList()))
				.thenReturn(Collections.singletonList(articleByStatusAndRoleResponseDto));

		mockMvc.perform(get("/articles/test-id/versions")
								.param("role", "USER")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].publicId").value("test-id"))
				.andExpect(jsonPath("$[0].title").value("Role Based Title"))
				.andExpect(jsonPath("$[0].description").value("Role Based Description"))
				.andExpect(jsonPath("$[0].content").value("Role Based Content"))
				.andExpect(jsonPath("$[0].version").value(1))
				.andExpect(jsonPath("$[0].status").value("SUBMITTED"))
				.andExpect(jsonPath("$[0].editedBy").value("testUser"));
	}

	@Test
	@WithMockUser
	public void testGetArticlesByStatusSubmitted_Success() throws Exception {
		Article submittedArticle = Article.builder()
				.publicId("test-id")
				.title("Submitted Title")
				.description("Submitted Description")
				.content("Submitted Content")
				.version(1)
				.status(Article.ArticleStatus.SUBMITTED)
				.editedBy("testUser")
				.build();

		ArticleResponseDto submittedArticleResponseDto = ArticleResponseDto.builder()
				.publicId("test-id")
				.title("Submitted Title")
				.description("Submitted Description")
				.content("Submitted Content")
				.version(1)
				.status(Article.ArticleStatus.SUBMITTED)
				.editedBy("testUser")
				.build();

		when(articleService.getArticlesByStatus()).thenReturn(Collections.singletonList(submittedArticle));
		when(articleMapper.mapToDtoList(anyList())).thenReturn(Collections.singletonList(submittedArticleResponseDto));

		mockMvc.perform(get("/articles")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].publicId").value("test-id"))
				.andExpect(jsonPath("$[0].title").value("Submitted Title"))
				.andExpect(jsonPath("$[0].description").value("Submitted Description"))
				.andExpect(jsonPath("$[0].content").value("Submitted Content"))
				.andExpect(jsonPath("$[0].version").value(1))
				.andExpect(jsonPath("$[0].status").value("SUBMITTED"))
				.andExpect(jsonPath("$[0].editedBy").value("testUser"));
	}

}
